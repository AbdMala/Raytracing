package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Constants;

public class CheckerBoard implements Shader {
    private final Shader a;
    private final Shader b;
    private final float scale ;

    public CheckerBoard(Shader a, Shader b, float scale) {
        if(Constants.isZero(scale)){
            throw new UnsupportedOperationException();
        }
        if(a == null || b == null || scale < 0f || Float.isInfinite(scale) || Float.isNaN(scale)){
            throw new IllegalArgumentException();
        }


        this.a = a;
        this.b = b;
        this.scale = scale;
    }

    @Override
    public Color shade(final Hit hit, final Trace trace) {
       final float x = (float) ( (Math.floor( hit.getUV().x() / scale)) + Math.floor( hit.getUV().y() / scale));
        final int y = (int)  x;
        if(y % 2 == 0){
            return a.shade(hit, trace);
        }

        else  return b.shade(hit, trace);
    }

}
