package io.playn.drop.core;

import java.util.Iterator;
import playn.core.Clock;
import playn.core.Image;
import playn.core.Keyboard;
import playn.core.Platform;
import playn.core.Sound;
import playn.core.Surface;
import playn.scene.ImageLayer;
import playn.scene.Layer;
import playn.scene.Mouse;
import playn.scene.Pointer;
import playn.scene.SceneGame;
import pythagoras.f.Rectangle;
import react.RList;

public class Drop extends SceneGame
{
	// assets
	private Image dropImage;
	private Image bucketImage;
	private Sound dropSound;
	private Sound rainMusic;

	// game elements
	private Rectangle bucket;
	private ImageLayer bucketLayer;
	private RList<Raindrop> raindrops;
	private long lastDropTimeMs;

	// input
	private boolean leftKeyPressed;
	private boolean rightKeyPressed;

	public Drop (Platform plat) {
		super(plat, 33); // update our "simulation" 33ms (30 times per second)

		// load the images for the droplet and the bucket, 64x64 pixels each
		dropImage = plat.assets().getImage("images/droplet.png");
		bucketImage = plat.assets().getImage("images/bucket.png");

		// load the drop sound effect and the rain background "music"
		dropSound = plat.assets().getSound("sounds/drop");
		rainMusic = plat.assets().getMusic("sounds/rain");

		// start the playback of the background music immediately
		rainMusic.setLooping(true);
		rainMusic.play();

		// our "virtual" game world will be 800x480 pixels. 
		// Figure out how big the device screen is, then stretch the root
		// layer so it fits the device screen in both dimensions
		float screenW = plat.graphics().viewSize.width();
		float screenH = plat.graphics().viewSize.height();
		float scaleX = screenW / 800;
		float scaleY = screenH / 480;
		rootLayer.setScale(scaleX, scaleY);

		// create the background layer to paint the background and receive
		// pointer events. Note that the layer must know its size in order
		// to participate in hit testing.
		Layer bgLayer = new Layer() {
			protected void paintImpl(Surface surf) {
				surf.setFillColor(0xff000033).fillRect(0, 0, 800, 480);
			}

			@Override
			public float width() {
				return 800;
			}

			@Override
			public float height() {
				return 480;
			}
		};
		rootLayer.add(bgLayer);

		// create bucket
		bucket = new Rectangle();
		bucket.x = 800 / 2 - 64 / 2;
		bucket.y = 480 - 20 - 64;
		bucket.width = 64;
		bucket.height = 64;

		bucketLayer = new ImageLayer(bucketImage);

		rootLayer.addAt(bucketLayer, bucket.x, bucket.y);

		// setup raindrops. We use a reactive list here so that adding
		// or removed a raindrop from the list automatically triggers the 
		// creation and removal of an associated ImageLayer.
		raindrops = RList.create();
		raindrops.connect(new RList.Listener<Raindrop>() {
			@Override
			public void onAdd(Raindrop drop) {
				drop.layer = new ImageLayer(dropImage);
				rootLayer.addAt(drop.layer, drop.rect.x, drop.rect.y);
			}
			
			@Override
			public void onRemove(Raindrop drop) {
				drop.layer.close();
			}
		});
		spawnRaindrop();

		// wire up pointer and mouse event dispatch
		new Pointer(plat, rootLayer, false);
		plat.input().mouseEvents.connect(new Mouse.Dispatcher(rootLayer, false));

		bgLayer.events().connect(new Pointer.Listener() {
			@Override
			public void onStart(Pointer.Interaction iact) {
				handlePointerEvent(iact);
			}

			@Override
			public void onDrag(Pointer.Interaction iact) {
				handlePointerEvent(iact);
			}
		});

		// wire keyboard events
		plat.input().keyboardEvents.connect(new Keyboard.KeySlot() {
			public void onEmit (Keyboard.KeyEvent ev) {
				switch (ev.key) {
					case LEFT: leftKeyPressed = ev.down; break;
					case RIGHT: rightKeyPressed = ev.down; break;
					default: break;
				}
			}
		});
	}

	@Override
	public void update(Clock t)
	{
		// 200 pixels / second = .2f pixels / ms
		float delta = .2f * t.dt;

		// handle bucket move via keyboard
		if (leftKeyPressed || rightKeyPressed)
		{
			updateBucketX(bucket.x + (leftKeyPressed ? (-delta) : (+delta)));
		}

		// check if we need to create a new raindrop
		if ((plat.tick() - lastDropTimeMs) > 1000)
			spawnRaindrop();

		// move the raindrops, remove any that are beneath the bottom edge of
		// the screen or that hit the bucket. In the later case we play back
		// a sound effect as well.
		Iterator<Raindrop> iter = raindrops.iterator();
		while (iter.hasNext())
		{
			Raindrop raindrop = iter.next();
			raindrop.rect.y += delta;

			if (raindrop.rect.y > 480)
			{
				iter.remove();
			}
			else if (raindrop.rect.intersects(bucket))
			{
				dropSound.play();
				iter.remove();
			}
			else
			{
				raindrop.layer.setTy(raindrop.rect.y);
			}
		}
	}

	private void handlePointerEvent(Pointer.Interaction iact)
	{
		// iact.local contains the event location, translated to the
		// layer's coordinate space
		float x = iact.local.x;
		
		updateBucketX(x - 64 / 2);
	}
	
	private void updateBucketX(float x)
	{
		bucket.x = x;
		if (bucket.x < 0) bucket.x = 0;
		if (bucket.x > 800 - 64) bucket.x = 800 - 64;
		bucketLayer.setTx(bucket.x);
	}

	private void spawnRaindrop()
	{
		Raindrop raindrop = new Raindrop();
		raindrop.rect = new Rectangle();
		raindrop.rect.x = (float) (Math.random() * (800-64));
		raindrop.rect.y = -64;
		raindrop.rect.width = 64;
		raindrop.rect.height = 64;

		raindrops.add(raindrop);

		lastDropTimeMs = plat.tick();
	}

	class Raindrop
	{
		Rectangle rect;
		ImageLayer layer;
	}
}
