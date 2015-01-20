/**
 * Copyright 2011 The PlayN Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package playn.showcase.core.peas;

import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.DebugDraw;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import react.Function;
import react.Functions;
import react.RFuture;
import react.Slot;

import playn.core.Canvas;
import playn.core.Clock;
import playn.core.DebugDrawBox2D;
import playn.core.Image;
import playn.core.Json;
import playn.core.Platform;
import playn.scene.GroupLayer;
import playn.scene.ImageLayer;

import playn.scene.LayerUtil;
import playn.showcase.core.Showcase;
import playn.showcase.core.peas.entities.*;

public class PeaWorld implements ContactListener {

  public final Platform plat;
  public final GroupLayer staticLayerBack;
  public final GroupLayer dynamicLayer;
  public final GroupLayer staticLayerFront;

  // size of world
  private static int width = 24;
  private static int height = 18;

  // box2d object containing physics world
  protected World world;

  private List<Entity> entities = new ArrayList<Entity>(0);
  private HashMap<Body, PhysicsEntity> bodyEntityLUT = new HashMap<Body, PhysicsEntity>();
  private Stack<Contact> contacts = new Stack<Contact>();

  private static boolean showDebugDraw = false;
  private DebugDrawBox2D debugDraw;
  private ImageLayer debugLayer;

  public PeaWorld (Showcase game, GroupLayer scaledLayer) {
    this.plat = game.plat;
    staticLayerBack = new GroupLayer();
    scaledLayer.add(staticLayerBack);
    dynamicLayer = new GroupLayer();
    scaledLayer.add(dynamicLayer);
    staticLayerFront = new GroupLayer();
    scaledLayer.add(staticLayerFront);

    // create the physics world
    Vec2 gravity = new Vec2(0.0f, 10.0f);
    world = new World(gravity);
    world.setWarmStarting(true);
    world.setAutoClearForces(true);
    world.setContactListener(this);

    // create the ground
    Body ground = world.createBody(new BodyDef());
    EdgeShape groundShape = new EdgeShape();
    groundShape.set(new Vec2(0, height), new Vec2(width, height));
    ground.createFixture(groundShape, 0.0f);

    // create the walls
    Body wallLeft = world.createBody(new BodyDef());
    EdgeShape wallLeftShape = new EdgeShape();
    wallLeftShape.set(new Vec2(0, 0), new Vec2(0, height));
    wallLeft.createFixture(wallLeftShape, 0.0f);
    Body wallRight = world.createBody(new BodyDef());
    EdgeShape wallRightShape = new EdgeShape();
    wallRightShape.set(new Vec2(width, 0), new Vec2(width, height));
    wallRight.createFixture(wallRightShape, 0.0f);

    // when our layer is connected, listen for frame events, disconnect when not
    LayerUtil.bind(scaledLayer, game.update, new Slot<Clock>() {
      public void onEmit (Clock clock) { update(clock); }
    }, game.paint, new Slot<Clock>() {
      public void onEmit (Clock clock) { paint(clock); }
    });
  }

  public RFuture<PeaWorld> loadLevel (final Platform plat, String levelName) {
    return plat.assets().getText(levelName).flatMap(new Function<String,RFuture<PeaWorld>>() {
      public RFuture<PeaWorld> apply (String levelJson) {
        return loadLevel(plat, plat.json().parse(levelJson));
      }
    });
  }

  private RFuture<PeaWorld> loadLevel (final Platform plat, Json.Object level) {
    // previous Portal (used for linking portals)
    Portal lastPortal = null;

    // track our various image loading states
    List<RFuture<Image>> states = new ArrayList<>();

    // parse the entities, adding each asset to the asset watcher
    Json.Array jsonEntities = level.getArray("Entities");
    for (int i = 0; i < jsonEntities.length(); i++) {
      Json.Object jsonEntity = jsonEntities.getObject(i);
      String type = jsonEntity.getString("type");
      float x = jsonEntity.getNumber("x");
      float y = jsonEntity.getNumber("y");
      float a = jsonEntity.getNumber("a");

      Entity entity = null;
      if (Pea.TYPE.equalsIgnoreCase(type)) {
        entity = new Pea(this, world, x, y, a);
      } else if (Block.TYPE.equalsIgnoreCase(type)) {
        entity = new Block(this, world, x, y, a);
      } else if (BlockRightRamp.TYPE.equalsIgnoreCase(type)) {
        entity = new BlockRightRamp(this, world, x, y, a);
      } else if (BlockLeftRamp.TYPE.equalsIgnoreCase(type)) {
        entity = new BlockLeftRamp(this, world, x, y, a);
      } else if (BlockGel.TYPE.equalsIgnoreCase(type)) {
        entity = new BlockGel(this, world, x, y, a);
      } else if (BlockSpring.TYPE.equalsIgnoreCase(type)) {
        entity = new BlockSpring(this, world, x, y, a);
      } else if (Cloud1.TYPE.equalsIgnoreCase(type)) {
        entity = new Cloud1(this);
      } else if (Cloud3.TYPE.equalsIgnoreCase(type)) {
        entity = new Cloud3(this);
      } else if (FakeBlock.TYPE.equalsIgnoreCase(type)) {
        entity = new FakeBlock(this, x, y, a);
      } else if (Portal.TYPE.equalsIgnoreCase(type)) {
        entity = new Portal(this, world, x, y, a);
        if (lastPortal == null) {
          lastPortal = (Portal) entity;
        } else {
          lastPortal.other = (Portal) entity;
          ((Portal) entity).other = lastPortal;
          lastPortal = null;
        }
      }

      if (entity != null) {
        states.add(entity.image.state);
        add(entity);
      }
    }

    // finally wait for all the images to load and then return the world
    return RFuture.collect(states).map(Functions.constant(this));
  }

  public void showDebugDraw (Showcase game) {
    debugDraw = new DebugDrawBox2D(game.plat, (int) (width / PeasDemo.physUnitPerScreenUnit),
                                   (int) (height / PeasDemo.physUnitPerScreenUnit));
    debugDraw.setFlipY(false);
    debugDraw.setStrokeAlpha(150);
    debugDraw.setFillAlpha(75);
    debugDraw.setStrokeWidth(2.0f);
    debugDraw.setFlags(DebugDraw.e_shapeBit | DebugDraw.e_jointBit | DebugDraw.e_aabbBit);
    debugDraw.setCamera(0, 0, 1f / PeasDemo.physUnitPerScreenUnit);
    game.rootLayer.add(debugLayer = new ImageLayer(debugDraw.canvas.image));
    world.setDebugDraw(debugDraw);
  }

  public void add (Entity entity) {
    entities.add(entity);
    if (entity instanceof PhysicsEntity) {
      PhysicsEntity physicsEntity = (PhysicsEntity) entity;
      bodyEntityLUT.put(physicsEntity.getBody(), physicsEntity);
    }
  }

  public Image getEntityImage (String name) {
    String path = "peas/images/" + name;
    Image image = entityImages.get(path);
    if (image == null) entityImages.put(path, image = plat.assets().getImage(path));
    return image;
  }
  private final Map<String,Image> entityImages = new HashMap<>();

  // handle contacts out of physics loop
  public void processContacts () {
    while (!contacts.isEmpty()) {
      Contact contact = contacts.pop();

      // handle collision
      PhysicsEntity entityA = bodyEntityLUT.get(contact.m_fixtureA.m_body);
      PhysicsEntity entityB = bodyEntityLUT.get(contact.m_fixtureB.m_body);

      if (entityA != null && entityB != null) {
        if (entityA instanceof PhysicsEntity.HasContactListener) {
          ((PhysicsEntity.HasContactListener) entityA).contact(entityB);
        }
        if (entityB instanceof PhysicsEntity.HasContactListener) {
          ((PhysicsEntity.HasContactListener) entityB).contact(entityA);
        }
      }
    }
  }

  // Box2d's begin contact
  @Override public void beginContact(Contact contact) {
    contacts.push(contact);
  }

  // Box2d's end contact
  @Override public void endContact(Contact contact) {}

  // Box2d's pre solve
  @Override public void preSolve(Contact contact, Manifold oldManifold) {}

  // Box2d's post solve
  @Override public void postSolve(Contact contact, ContactImpulse impulse) {}

  private void update (Clock clock) {
    for (Entity e : entities) e.update(clock);
    // the step delta is fixed so box2d isn't affected by framerate
    world.step(clock.dt/1000f, 10, 10);
    processContacts();
  }

  private void paint (Clock clock) {
    if (showDebugDraw) {
      debugDraw.canvas.clear();
      world.drawDebugData();
      debugLayer.tile().texture().update(debugDraw.canvas.image);
    }
    for (Entity e : entities) e.paint(clock);
  }
}
