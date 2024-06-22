package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.ViewportAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

public class FDS_SpriteRenderManager extends BaseEveryFrameCombatPlugin {
   private static List<FDS_SpriteRenderManager.renderData> SINGLEFRAME = new ArrayList();
   private static List<FDS_SpriteRenderManager.battlespaceData> BATTLESPACE = new ArrayList();
   private static List<FDS_SpriteRenderManager.objectspaceData> OBJECTSPACE = new ArrayList();
   private static List<FDS_SpriteRenderManager.screenspaceData> SCREENSPACE = new ArrayList();

   public void init(CombatEngineAPI engine) {
      SINGLEFRAME.clear();
      BATTLESPACE.clear();
      OBJECTSPACE.clear();
      SCREENSPACE.clear();
   }

   public static void singleFrameRender(SpriteAPI sprite, Vector2f loc, Vector2f size, float angle, Color color, boolean additive) {
      sprite.setSize(size.x, size.y);
      sprite.setAngle(angle);
      sprite.setColor(color);
      if (additive) {
         sprite.setAdditiveBlend();
      }

      SINGLEFRAME.add(new FDS_SpriteRenderManager.renderData(sprite, loc));
   }

   public static void battlespaceRender(SpriteAPI sprite, Vector2f loc, Vector2f vel, Vector2f size, Vector2f growth, float angle, float spin, Color color, boolean additive, float fadein, float full, float fadeout) {
      sprite.setSize(size.x, size.y);
      sprite.setAngle(angle);
      sprite.setColor(color);
      if (additive) {
         sprite.setAdditiveBlend();
      }

      Vector2f velocity = new Vector2f(vel);
      BATTLESPACE.add(new FDS_SpriteRenderManager.battlespaceData(sprite, loc, velocity, growth, spin, fadein, fadein + full, fadein + full + fadeout, 0.0F));
   }

   public static void objectspaceRender(SpriteAPI sprite, CombatEntityAPI anchor, Vector2f offset, Vector2f vel, Vector2f size, Vector2f growth, float angle, float spin, boolean parent, Color color, boolean additive, float fadein, float full, float fadeout, boolean fadeOnDeath) {
      sprite.setSize(size.x, size.y);
      if (parent) {
         sprite.setAngle(anchor.getFacing() + angle + 90.0F);
      } else {
         sprite.setAngle(angle + 90.0F);
      }

      sprite.setColor(color);
      if (additive) {
         sprite.setAdditiveBlend();
      }

      Vector2f loc = new Vector2f(50000.0F, 50000.0F);
      if (anchor.getLocation() != null) {
         loc = new Vector2f(anchor.getLocation());
      }

      Vector2f velocity = new Vector2f(vel);
      OBJECTSPACE.add(new FDS_SpriteRenderManager.objectspaceData(sprite, anchor, loc, offset, velocity, growth, angle, spin, parent, fadein, fadein + full, fadein + full + fadeout, fadeOnDeath, 0.0F));
   }

   public static void screenspaceRender(SpriteAPI sprite, FDS_SpriteRenderManager.positioning pos, Vector2f loc, Vector2f vel, Vector2f size, Vector2f growth, float angle, float spin, Color color, boolean additive, float fadein, float full, float fadeout) {
      ViewportAPI screen = Global.getCombatEngine().getViewport();
      Vector2f ratio = size;
      Vector2f screenSize = new Vector2f(screen.getVisibleWidth(), screen.getVisibleHeight());
      if (pos == FDS_SpriteRenderManager.positioning.STRETCH_TO_FULLSCREEN) {
         sprite.setSize(screenSize.x, screenSize.y);
      } else if (pos == FDS_SpriteRenderManager.positioning.FULLSCREEN_MAINTAIN_RATIO) {
         if (size.x / size.y > screenSize.x / screenSize.y) {
            ratio = new Vector2f(size.x / size.y / (screenSize.x / screenSize.y), 1.0F);
         } else {
            ratio = new Vector2f(1.0F, size.y / size.x / (screenSize.y / screenSize.x));
            sprite.setSize(Global.getCombatEngine().getViewport().getVisibleWidth() * ratio.x, Global.getCombatEngine().getViewport().getVisibleHeight() * ratio.y);
         }
      } else {
         sprite.setSize(size.x * screen.getViewMult(), size.y * screen.getViewMult());
      }

      sprite.setAngle(angle);
      sprite.setColor(color);
      if (additive) {
         sprite.setAdditiveBlend();
      }

      Vector2f velocity = new Vector2f(vel);
      SCREENSPACE.add(new FDS_SpriteRenderManager.screenspaceData(sprite, pos, loc, velocity, ratio, growth, spin, fadein, fadein + full, fadein + full + fadeout, 0.0F));
   }

   public void renderInWorldCoords(ViewportAPI view) {
      CombatEngineAPI engine = Global.getCombatEngine();
      if (engine != null) {
         float amount = 0.0F;
         if (!engine.isPaused()) {
            amount = engine.getElapsedInLastFrame();
         }

         Iterator iter;
         Vector2f location;
         if (!BATTLESPACE.isEmpty()) {
            iter = BATTLESPACE.iterator();

            label249:
            while(true) {
               while(true) {
                  if (!iter.hasNext()) {
                     break label249;
                  }

                  FDS_SpriteRenderManager.battlespaceData entry = (FDS_SpriteRenderManager.battlespaceData)iter.next();
                  entry.TIME = amount;
                  if (entry.TIME > entry.FADEOUT) {
                     iter.remove();
                  } else {
                     if (entry.GROWTH != null && entry.GROWTH != new Vector2f()) {
                        entry.SPRITE.setSize(entry.SPRITE.getWidth() + entry.GROWTH.x * amount, entry.SPRITE.getHeight() + entry.GROWTH.y * amount);
                        if (entry.SPRITE.getHeight() <= 0.0F || entry.SPRITE.getWidth() <= 0.0F) {
                           iter.remove();
                           continue;
                        }
                     }

                     if (entry.VEL != null && entry.VEL != new Vector2f()) {
                        location = new Vector2f(entry.VEL);
                        location.scale(amount);
                        Vector2f.add(entry.LOC, location, entry.LOC);
                     }

                     if (entry.SPIN != 0.0F) {
                        entry.SPRITE.setAngle(entry.SPRITE.getAngle() + entry.SPIN * amount);
                     }

                     if (entry.TIME < entry.FADEIN) {
                        entry.SPRITE.setAlphaMult(entry.TIME / entry.FADEIN);
                     } else if (entry.TIME > entry.FULL) {
                        entry.SPRITE.setAlphaMult(1.0F - (entry.TIME - entry.FULL) / (entry.FADEOUT - entry.FULL));
                     } else {
                        entry.SPRITE.setAlphaMult(1.0F);
                     }

                     this.render(new FDS_SpriteRenderManager.renderData(entry.SPRITE, entry.LOC));
                  }
               }
            }
         }

         if (!OBJECTSPACE.isEmpty()) {
            iter = OBJECTSPACE.iterator();

            label227:
            while(true) {
               while(true) {
                  if (!iter.hasNext()) {
                     break label227;
                  }

                  FDS_SpriteRenderManager.objectspaceData entry = (FDS_SpriteRenderManager.objectspaceData)iter.next();
                  if (!entry.DEATHFADE && !engine.isEntityInPlay(entry.ANCHOR)) {
                     iter.remove();
                  } else {
                     if (entry.ANCHOR instanceof DamagingProjectileAPI && entry.TIME < entry.FULL && (((DamagingProjectileAPI)entry.ANCHOR).isFading() || !engine.isEntityInPlay(entry.ANCHOR))) {
                        entry.FADEOUT = entry.FADEOUT - entry.FULL + entry.TIME;
                     }

                     entry.TIME = amount;
                     if (entry.TIME > entry.FADEOUT) {
                        iter.remove();
                     } else {
                        if (entry.GROWTH != null && entry.GROWTH != new Vector2f()) {
                           entry.SPRITE.setSize(entry.SPRITE.getWidth() + entry.GROWTH.x * amount, entry.SPRITE.getHeight() + entry.GROWTH.y * amount);
                           if (entry.SPRITE.getHeight() <= 0.0F || entry.SPRITE.getWidth() <= 0.0F) {
                              iter.remove();
                              continue;
                           }
                        }

                        if (entry.VEL != null && entry.VEL != new Vector2f()) {
                           location = new Vector2f(entry.VEL);
                           location.scale(amount);
                           Vector2f.add(entry.OFFSET, location, location);
                           entry.OFFSET = location;
                        }

                        location = new Vector2f(entry.OFFSET);
                        if (entry.PARENT && engine.isEntityInPlay(entry.ANCHOR)) {
                           if (entry.SPIN != 0.0F) {
                              entry.ANGLE = entry.SPIN * amount;
                           }

                           entry.SPRITE.setAngle(entry.ANCHOR.getFacing() + 90.0F + entry.ANGLE);
                           VectorUtils.rotate(location, entry.ANCHOR.getFacing(), location);
                        } else if (entry.SPIN != 0.0F) {
                           entry.SPRITE.setAngle(entry.SPRITE.getAngle() + entry.SPIN * amount);
                        }

                        if (entry.PARENT && engine.isEntityInPlay(entry.ANCHOR)) {
                           Vector2f loc = new Vector2f(entry.ANCHOR.getLocation());
                           Vector2f.add(location, loc, location);
                           entry.LOCATION = loc;
                        } else {
                           Vector2f.add(location, entry.LOCATION, location);
                        }

                        if (entry.TIME < entry.FADEIN) {
                           entry.SPRITE.setAlphaMult(entry.TIME / entry.FADEIN);
                        } else if (entry.TIME > entry.FULL) {
                           entry.SPRITE.setAlphaMult(1.0F - (entry.TIME - entry.FULL) / (entry.FADEOUT - entry.FULL));
                        } else {
                           entry.SPRITE.setAlphaMult(1.0F);
                        }

                        this.render(new FDS_SpriteRenderManager.renderData(entry.SPRITE, location));
                     }
                  }
               }
            }
         }

         if (!SCREENSPACE.isEmpty()) {
            ViewportAPI screen = Global.getCombatEngine().getViewport();
            iter = SCREENSPACE.iterator();

            label185:
            while(true) {
               while(true) {
                  if (!iter.hasNext()) {
                     break label185;
                  }

                  FDS_SpriteRenderManager.screenspaceData entry = (FDS_SpriteRenderManager.screenspaceData)iter.next();
                  Vector2f refPoint;
                  Vector2f center;
                  if (entry.FADEOUT < 0.0F) {
                     if (entry.POS == FDS_SpriteRenderManager.positioning.FULLSCREEN_MAINTAIN_RATIO) {
                        center = new Vector2f(screen.getCenter());
                        entry.SPRITE.setSize(entry.SIZE.x * screen.getVisibleWidth(), entry.SIZE.y * screen.getVisibleHeight());
                     } else if (entry.POS == FDS_SpriteRenderManager.positioning.STRETCH_TO_FULLSCREEN) {
                        center = new Vector2f(screen.getCenter());
                        entry.SPRITE.setSize(screen.getVisibleWidth(), screen.getVisibleHeight());
                     } else {
                        refPoint = screen.getCenter();
                        switch(entry.POS) {
                        case LOW_LEFT:
                           refPoint = new Vector2f(refPoint.x - screen.getVisibleWidth() / 2.0F, refPoint.y - screen.getVisibleHeight() / 2.0F);
                           break;
                        case LOW_RIGHT:
                           refPoint = new Vector2f(refPoint.x - screen.getVisibleWidth() / 2.0F, refPoint.y + screen.getVisibleHeight() / 2.0F);
                           break;
                        case UP_LEFT:
                           refPoint = new Vector2f(refPoint.x + screen.getVisibleWidth() / 2.0F, refPoint.y - screen.getVisibleHeight() / 2.0F);
                           break;
                        case UP_RIGHT:
                           refPoint = new Vector2f(refPoint.x + screen.getVisibleWidth() / 2.0F, refPoint.y + screen.getVisibleHeight() / 2.0F);
                        }

                        center = new Vector2f(entry.LOC);
                        center.scale(screen.getViewMult());
                        Vector2f.add(center, refPoint, center);
                     }

                     this.render(new FDS_SpriteRenderManager.renderData(entry.SPRITE, center));
                     iter.remove();
                  } else {
                     entry.TIME = amount;
                     if (entry.FADEOUT > 0.0F && entry.TIME > entry.FADEOUT) {
                        iter.remove();
                     } else {
                        if (entry.POS == FDS_SpriteRenderManager.positioning.FULLSCREEN_MAINTAIN_RATIO) {
                           center = new Vector2f(screen.getCenter());
                           entry.SPRITE.setSize(entry.SIZE.x * screen.getVisibleWidth(), entry.SIZE.y * screen.getVisibleHeight());
                        } else if (entry.POS == FDS_SpriteRenderManager.positioning.STRETCH_TO_FULLSCREEN) {
                           center = new Vector2f(screen.getCenter());
                           entry.SPRITE.setSize(screen.getVisibleWidth(), screen.getVisibleHeight());
                        } else {
                           refPoint = screen.getCenter();
                           switch(entry.POS) {
                           case LOW_LEFT:
                              refPoint = new Vector2f(refPoint.x - screen.getVisibleWidth() / 2.0F, refPoint.y - screen.getVisibleHeight() / 2.0F);
                              break;
                           case LOW_RIGHT:
                              refPoint = new Vector2f(refPoint.x - screen.getVisibleWidth() / 2.0F, refPoint.y + screen.getVisibleHeight() / 2.0F);
                              break;
                           case UP_LEFT:
                              refPoint = new Vector2f(refPoint.x + screen.getVisibleWidth() / 2.0F, refPoint.y - screen.getVisibleHeight() / 2.0F);
                              break;
                           case UP_RIGHT:
                              refPoint = new Vector2f(refPoint.x + screen.getVisibleWidth() / 2.0F, refPoint.y + screen.getVisibleHeight() / 2.0F);
                           }

                           if (entry.VEL != null && entry.VEL != new Vector2f()) {
                              Vector2f move = new Vector2f(entry.VEL);
                              move.scale(amount);
                              Vector2f.add(entry.LOC, move, entry.LOC);
                           }

                           center = new Vector2f(entry.LOC);
                           center.scale(screen.getViewMult());
                           Vector2f.add(center, refPoint, center);
                           if (entry.GROWTH != null && entry.GROWTH != new Vector2f()) {
                              entry.SIZE = new Vector2f(entry.SIZE.x + entry.GROWTH.x * amount, entry.SIZE.y + entry.GROWTH.y * amount);
                              if (entry.SIZE.x <= 0.0F || entry.SIZE.y <= 0.0F) {
                                 iter.remove();
                                 continue;
                              }
                           }

                           entry.SPRITE.setSize(entry.SIZE.x * screen.getViewMult(), entry.SIZE.y * screen.getViewMult());
                           if (entry.SPIN != 0.0F) {
                              entry.SPRITE.setAngle(entry.SPRITE.getAngle() + entry.SPIN * amount);
                           }
                        }

                        if (entry.TIME < entry.FADEIN) {
                           entry.SPRITE.setAlphaMult(entry.TIME / entry.FADEIN);
                        } else if (entry.TIME > entry.FULL) {
                           entry.SPRITE.setAlphaMult(1.0F - (entry.TIME - entry.FULL) / (entry.FADEOUT - entry.FULL));
                        } else {
                           entry.SPRITE.setAlphaMult(1.0F);
                        }

                        this.render(new FDS_SpriteRenderManager.renderData(entry.SPRITE, center));
                        if (entry.FADEOUT < 0.0F) {
                           iter.remove();
                        }
                     }
                  }
               }
            }
         }

         if (!SINGLEFRAME.isEmpty()) {
            iter = SINGLEFRAME.iterator();

            while(iter.hasNext()) {
               FDS_SpriteRenderManager.renderData d = (FDS_SpriteRenderManager.renderData)iter.next();
               this.render(d);
            }

            SINGLEFRAME.clear();
         }

      }
   }

   private void render(FDS_SpriteRenderManager.renderData data) {
      SpriteAPI sprite = data.SPRITE;
      sprite.renderAtCenter(data.LOC.x, data.LOC.y);
   }

   private static class screenspaceData {
      private final SpriteAPI SPRITE;
      private final FDS_SpriteRenderManager.positioning POS;
      private Vector2f LOC;
      private final Vector2f VEL;
      private Vector2f SIZE;
      private final Vector2f GROWTH;
      private final float SPIN;
      private final float FADEIN;
      private final float FULL;
      private final float FADEOUT;
      private float TIME;

      public screenspaceData(SpriteAPI sprite, FDS_SpriteRenderManager.positioning position, Vector2f loc, Vector2f vel, Vector2f size, Vector2f growth, float spin, float fadein, float full, float fadeout, float time) {
         this.SPRITE = sprite;
         this.POS = position;
         this.LOC = loc;
         this.VEL = vel;
         this.SIZE = size;
         this.GROWTH = growth;
         this.SPIN = spin;
         this.FADEIN = fadein;
         this.FULL = full;
         this.FADEOUT = fadeout;
         this.TIME = time;
      }
   }

   public static enum positioning {
      CENTER,
      LOW_LEFT,
      LOW_RIGHT,
      UP_LEFT,
      UP_RIGHT,
      STRETCH_TO_FULLSCREEN,
      FULLSCREEN_MAINTAIN_RATIO;
   }

   private static class objectspaceData {
      private final SpriteAPI SPRITE;
      private final CombatEntityAPI ANCHOR;
      private Vector2f LOCATION;
      private Vector2f OFFSET;
      private final Vector2f VEL;
      private final Vector2f GROWTH;
      private float ANGLE;
      private final float SPIN;
      private final boolean PARENT;
      private final float FADEIN;
      private float FULL;
      private float FADEOUT;
      private final boolean DEATHFADE;
      private float TIME;

      public objectspaceData(SpriteAPI sprite, CombatEntityAPI anchor, Vector2f loc, Vector2f offset, Vector2f vel, Vector2f growth, float angle, float spin, boolean parent, float fadein, float full, float fadeout, boolean fade, float time) {
         this.SPRITE = sprite;
         this.ANCHOR = anchor;
         this.LOCATION = loc;
         this.OFFSET = offset;
         this.VEL = vel;
         this.GROWTH = growth;
         this.ANGLE = angle;
         this.SPIN = spin;
         this.PARENT = parent;
         this.FADEIN = fadein;
         this.FULL = full;
         this.FADEOUT = fadeout;
         this.DEATHFADE = fade;
         this.TIME = time;
      }
   }

   private static class battlespaceData {
      private final SpriteAPI SPRITE;
      private Vector2f LOC;
      private final Vector2f VEL;
      private final Vector2f GROWTH;
      private final float SPIN;
      private final float FADEIN;
      private final float FULL;
      private final float FADEOUT;
      private float TIME;

      public battlespaceData(SpriteAPI sprite, Vector2f loc, Vector2f vel, Vector2f growth, float spin, float fadein, float full, float fadeout, float time) {
         this.SPRITE = sprite;
         this.LOC = loc;
         this.VEL = vel;
         this.GROWTH = growth;
         this.SPIN = spin;
         this.FADEIN = fadein;
         this.FULL = full;
         this.FADEOUT = fadeout;
         this.TIME = time;
      }
   }

   private static class renderData {
      private final SpriteAPI SPRITE;
      private final Vector2f LOC;

      public renderData(SpriteAPI sprite, Vector2f loc) {
         this.SPRITE = sprite;
         this.LOC = loc;
      }
   }
}
