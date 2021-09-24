package raytracer.core;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import raytracer.core.def.Accelerator;
import raytracer.core.def.StandardObj;
import raytracer.geom.GeomFactory;
import raytracer.geom.Primitive;
import raytracer.math.Color;
import raytracer.math.Point;
import raytracer.math.Vec3;
import raytracer.shade.SingleColor;

import javax.swing.plaf.IconUIResource;

/**
 * Represents a model file reader for the OBJ format
 */
public class OBJReader {

	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param filename
	 *            The file to read the data from
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *             If the filename is null or the empty string, the accelerator
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final String filename,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		read(new BufferedInputStream(new FileInputStream(filename)), accelerator, shader, scale, translate);
	}


	/**
	 * Reads an OBJ file and uses the given shader for all triangles. While
	 * loading the triangles they are inserted into the given acceleration
	 * structure accelerator.
	 *
	 * @param in
	 *            The InputStream of the data to be read.
	 * @param accelerator
	 *            The target acceleration structure
	 * @param shader
	 *            The shader which is used by all triangles
	 * @param scale
	 *            The scale factor which is responsible for scaling the model
	 * @param translate
	 *            A vector representing the translation coordinate with which
	 *            all coordinates have to be translated
	 * @throws IllegalArgumentException
	 *             If the InputStream is null, the accelerator
	 *             is null, the shader is null, the translate vector is null,
	 *             the translate vector is not finite or scale does not
	 *             represent a legal (finite) floating point number
	 */
	public static void read(final InputStream in,
			final Accelerator accelerator, final Shader shader, final float scale,
			final Vec3 translate) throws FileNotFoundException {
		Scanner inputfile ;
		inputfile = new Scanner(in);
		inputfile.useLocale(Locale.ENGLISH);
		List<Point> vertix = new ArrayList<>();
		vertix.add(new Point(0, 0, 0));

		while (inputfile.hasNext()){
			String next = inputfile.next();
			if(next.equals("#"))
			{
				inputfile.nextLine();
				continue;

			}else if(next.equals("v")) {
				Point temp = new Point(inputfile.nextFloat() , inputfile.nextFloat() , inputfile.nextFloat()).scale(scale).add(translate);
				vertix.add(temp);
			}
			else if(next.equals("f"))
			{
				final Primitive tri = GeomFactory.createTriangle(vertix.get(inputfile.nextInt()),vertix.get(inputfile.nextInt()),vertix.get(inputfile.nextInt()));
				final Obj       triangle = new StandardObj(tri, shader);
				accelerator.add(triangle);
			} else
				inputfile.nextLine();
		}



	}
}
