package raytracer.geom;

import raytracer.core.Hit;
import raytracer.core.Obj;
import raytracer.core.def.LazyHitTest;
import raytracer.math.Constants;
import raytracer.math.Point;
import raytracer.math.Ray;
import raytracer.math.Vec2;
import raytracer.math.Vec3;

import static raytracer.geom.Util.computeSphereUV;

class Sphere extends BBoxedPrimitive {
    private final Point m;
    private final float r;

    public Sphere(Point m, float r) {
        Point min = new Point(m.x() - r, m.y() - r, m.z() - r);
        Point max = new Point(m.x() + r, m.y() + r, m.z() + r);
        setBBox(BBox.create(min , max));
        this.m = m;
        this.r = r;

    }

    @Override
    public Hit hitTest(final Ray ray, final Obj obj, final float tmin, final float tmax) {
        return new LazyHitTest(obj) {
            private Point point = null;
            private float lam;
            private Vec3 n;
            
            @Override
            public float getParameter() {
                return lam;
            }

            @Override
            public Point getPoint() {
                if (point == null) {
                    n = ray.eval(lam).sub(m);
                    n = n.normalized();
                    point = ray.eval(lam).add(n.scale(0.0001f));
                }
                return point;
            }

            @Override
            protected boolean calculateHit() {
                final Vec3 /* normalized */ Vs = ray.dir();
                final Point Ps = ray.base();
                final float b = Vs.scale(2).dot(Ps.sub(m)) ;
                final float c = Ps.sub(m).dot(Ps.sub(m)) - (r *r) ;
                final float test = (float) Math.sqrt((b*b) - (4*c));
                if(test<Constants.EPS) {
                    return false ;
                }
                final  float lam1 = (-b + test) / 2f ;
                final  float lam2 = (-b - test) / 2f ;
                lam = Math.min(lam1,lam2) ;
                if (lam < tmin || lam > tmax)
                    return false;
                return lam >= Constants.EPS;
            }

            @Override
            public Vec2 getUV() {
                Vec3 from_center = getPoint().sub(m);
                return computeSphereUV(from_center);
            }

            @Override
            public Vec3 getNormal() {
                n = ray.eval(lam).sub(m);
                n = n.normalized();
                return n;
            }

        };
    }

    @Override
    public int hashCode() {
        return m.hashCode() ^ Float.floatToIntBits(r) ;
    }

    @Override
    public boolean equals(final Object other) {
        if (other instanceof Sphere) {
            boolean x = Constants.isZero(Math.abs(((Sphere) other).r - r ));
            return ((Sphere) other).m.equals(m) &&  x;
        }
        return false;
    }

}
