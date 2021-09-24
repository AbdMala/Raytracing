package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

import static raytracer.geom.Util.computePlaneUV;

class Plane extends BBoxedPrimitive {

    private final Point pe;
    private final float d;
    private final Vec3 ne ;


    public Plane(final Point a, final Point b, final Point c) {
        super(BBox.INF);
        this.pe = a;
        Vec3 ue = b.sub(a);
        Vec3  ve = c.sub(a);
        this.ne = ve.cross(ue).normalized();
        this.d = pe.dot(ne);
    }

    public Plane(Vec3 n, Point supp) {
        super(BBox.INF);
        this.ne = n.normalized();
        this.pe = supp;
        this.d = pe.dot(ne);
    }

    @Override
    public Hit hitTest(final Ray ray, final Obj obj, final float tmin, final float tmax) {
        return new LazyHitTest(obj) {
            private Point point = null;
            private float lam;

            @Override
            public float getParameter() {
                return lam;
            }

            @Override
            public Point getPoint() {
                if (point == null)
                    point = ray.eval(lam).add(ne.scale(0.0001f));
                return point;
            }

            @Override
            protected boolean calculateHit() {
                final Vec3 /* normalized */Vs = ray.dir();
                final Point Ps = ray.base();

                float po;
                po = Vs.dot(ne) ;

                if (Constants.isZero(po))
                    return false;

                lam = (d - Ps.dot(ne)) / po ;

            
                if (lam < tmin || lam > tmax)
                    return false;

                if (lam >= Constants.EPS) return true;
                else return false;
            }

            @Override
            public Vec2 getUV() {
                return computePlaneUV(ne ,pe,getPoint());
            }

            @Override
            public Vec3 getNormal() {
                return ne ;
            }

        };
    }


    @Override
    public int hashCode() {
        final int i = Float.floatToIntBits(d) ^ ne.hashCode();
        return i;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Plane) {
            boolean x = Constants.isZero(Math.abs(((Plane) other).d - d ));
            return ((Plane) other).ne.equals(ne) &&  x;
        }
        return false;
    }
}
