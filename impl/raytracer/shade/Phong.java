package raytracer.shade;

import raytracer.core.Hit;
import raytracer.core.LightSource;
import raytracer.core.Shader;
import raytracer.core.Trace;
import raytracer.math.Color;
import raytracer.math.Constants;

import java.util.Collection;

public class Phong implements Shader {

    private final Shader inner;
    private final Color ambient;
    private final float diffuse;
    private final float specular;
    private final float shininess;

    public Phong(Shader inner, Color ambient, float diffuse, float specular, float shininess) {
        if (inner == null || ambient == null || diffuse < 0f || specular < 0f || shininess < 0f || Float.isInfinite(diffuse) ||
                Float.isInfinite(specular) || Float.isInfinite(shininess) ||
        Float.isNaN(shininess) || Float.isNaN(diffuse) || Float.isNaN(specular)) {
            throw new IllegalArgumentException();
        }
        this.inner = inner;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }

    @Override
    public Color shade(final Hit hit, final Trace trace) {
        var light_sourses = trace.getScene().getLightSources();
        Color diffu = new Color(0, 0, 0);
        Color spect = new Color(0, 0, 0);
        for (var x : light_sourses) {
            Color sub = inner.shade(hit, trace);
            Trace shatten = trace.spawn(hit.getPoint(), (x.getLocation().sub(hit.getPoint())).normalized());
            if (shatten.getHit().hits()) continue;
            diffu = diffu.add(x.getColor().mul(sub).scale(diffuse).scale(Math.max(0f, hit.getNormal().dot(x.getLocation().sub(hit.getPoint()).normalized()))));
            spect = spect.add(x.getColor().scale(specular).scale((float) Math.pow(Math.max(0f, trace.getRay().dir().reflect(hit.getNormal()).dot(x.getLocation().sub(hit.getPoint()).normalized())), shininess)));

        }
        return ambient.add(diffu).add(spect);
    }

}
