package uk.ac.dundee.computing.kb.burnigram.models;

import static org.imgscalr.Scalr.OP_ANTIALIAS;
import static org.imgscalr.Scalr.OP_BRIGHTER;
import static org.imgscalr.Scalr.OP_DARKER;
import static org.imgscalr.Scalr.OP_GRAYSCALE;
import static org.imgscalr.Scalr.pad;
import static org.imgscalr.Scalr.resize;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Rotation;

import uk.ac.dundee.computing.kb.burnigram.dbHelpers.PicDbHelper;
import uk.ac.dundee.computing.kb.burnigram.lib.Convertors;
import uk.ac.dundee.computing.kb.burnigram.stores.Pic;

import com.jhlabs.image.EdgeFilter;
import com.jhlabs.image.GrayFilter;
import com.jhlabs.image.InvertFilter;

/**
 * This class is used as a controller 
 * to apply several manipulations on a {@link Pic} 
 *
 */
public class PicController {

	private static final String ROTATE = "rotate";
	private static final String BRIGHTNESS = "brightness";
	private static final String ORIGINAL ="original";
	private static final String LEFT = "left";
	private static final String RIGHT = "right";
	private static final String DARK = "dark";
	private static final String BRIGHT = "bright";

	
	private int imageTypeFlag = 0;

	private static final List<String> manipulationKeyList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(ROTATE);
		myList.add(BRIGHTNESS);
		myList.add(ORIGINAL);
		manipulationKeyList = Collections.unmodifiableList(myList);
	}

	private static final List<String> rotationOperationsList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(LEFT);
		myList.add(RIGHT);
		rotationOperationsList = Collections.unmodifiableList(myList);
	}

	private static final List<String> brightnessOperationsList;
	static {
		LinkedList<String> myList = new LinkedList<String>();
		myList.add(DARK);
		myList.add(BRIGHT);
		brightnessOperationsList = Collections.unmodifiableList(myList);
	}
	


	public PicController() {
		
	}



	public static BufferedImage blackAndWhite(BufferedImage img) {
		img = org.imgscalr.Scalr.apply(img, OP_GRAYSCALE);
		return img;
	}

	public static BufferedImage createThumbnail(BufferedImage img) {
		img = resize(img, Method.BALANCED, 250, OP_ANTIALIAS);
		// Let's add a little border before we return result.
		return pad(img, 2);
	}

	public static BufferedImage createProcessed(BufferedImage img) {
		int Width = img.getWidth() - 1;
		img = resize(img, Method.QUALITY, Width, OP_ANTIALIAS);
		return pad(img, 4);
	}

	public static BufferedImage rotate(BufferedImage buffImage, String direction) {
		if (!stringInStringList(direction, rotationOperationsList)) {
			System.err.println("rotate Picture  invalid manipulation value "
					+ direction);
			return buffImage;
		}

		switch (direction) {
		case LEFT:
			buffImage = org.imgscalr.Scalr.rotate(buffImage, Rotation.CW_270);
			break;
		case RIGHT:
			buffImage = org.imgscalr.Scalr.rotate(buffImage, Rotation.CW_90);
			break;
		}

		return buffImage;
	}

	public static BufferedImage changeBrighteness(BufferedImage buffImage,
			String brightness) {
		if (!stringInStringList(brightness, brightnessOperationsList)) {
			System.err
					.println("picture update brightness invalid manipulation value "
							+ brightness);
			return buffImage;
		}
		switch (brightness) {
		case DARK:
			buffImage = org.imgscalr.Scalr.apply(buffImage, OP_DARKER);
			break;
		case BRIGHT:
			buffImage = org.imgscalr.Scalr.apply(buffImage, OP_BRIGHTER);
			break;
		}
		return buffImage;
	}
	
	public static BufferedImage changeContrast(BufferedImage buffImage, String contrast){
		EdgeFilter edgeFilter = new EdgeFilter();
		edgeFilter.setHEdgeMatrix(EdgeFilter.SOBEL_V);
//		ContrastFilter contrastFilter = new ContrastFilter();
//		BufferedImage dest = contrastFilter.createCompatibleDestImage(buffImage, null);
//		float contrastValue = Float.parseFloat(contrast);
//		System.out.println("contrast from req:"+Float.toString(contrastValue));
//		float appliedContrast = (float) (contrastValue*0.1);
//		System.out.println("applied contrast"+Float.toString(appliedContrast));
//		contrastFilter.setContrast(0.55f);
	
		BufferedImage dest = edgeFilter.createCompatibleDestImage(buffImage, null);
		edgeFilter.filter(buffImage, dest);
		edgeFilter.filter(buffImage, dest);
		GrayFilter greyFilter = new GrayFilter();
		BufferedImage greyDest = greyFilter.createCompatibleDestImage(dest, null);
		greyFilter.filter(dest, greyDest);
		
		InvertFilter invertFilter = new InvertFilter();
		BufferedImage invDest = invertFilter.createCompatibleDestImage(greyDest, null);
		invertFilter.filter(greyDest, invDest);
		
		 
		return invDest;
	}
	
	public static BufferedImage  revertToOriginal(UUID picid, final int type){
		PicDbHelper picDbHelper= new PicDbHelper();
		Pic originalPic = picDbHelper.getPicFromDB(Convertors.DISPLAY_ORIGINAL_IMAGE, picid);
		BufferedImage original = Convertors.byteArrayToBufferedImage(originalPic.getBytes());
		BufferedImage returnImage = null;
		switch(type){
		case Convertors.DISPLAY_PROCESSED:
			returnImage = createProcessed(original);
			break;
		case Convertors.DISPLAY_THUMB:
			returnImage = createThumbnail(original);
			break;
		default:
				System.err.println("revert to Original invalid type");
		}
		return returnImage;
		
	}
	
	
	public BufferedImage manipulatePic(Pic pic,
			Entry<String, String> typeOfManipulation) {
		final String manipulationKey = typeOfManipulation.getKey();
		final String manipulationValue = typeOfManipulation.getValue();
		if (!stringInStringList(manipulationKey, manipulationKeyList)) {
			System.err.println("update Picture invalid manipulation type "
					+ manipulationKey);
			return null;
		}

		BufferedImage manipulatedBuffImg = Convertors.byteArrayToBufferedImage(pic
				.getBytes());
		switch (manipulationKey) {
		case ROTATE:
			manipulatedBuffImg = rotate(manipulatedBuffImg, manipulationValue);

			break;
		case BRIGHTNESS:
			manipulatedBuffImg = changeBrighteness(manipulatedBuffImg,
					manipulationValue);
			break;
//		case CONTRAST:
//			manipulatedBuffImg = changeContrast(manipulatedBuffImg,
//					manipulationValue);
//			break;
		case ORIGINAL:
			if(imageTypeFlag == 0){
				manipulatedBuffImg=revertToOriginal(pic.getUUID(), Convertors.DISPLAY_PROCESSED);
				imageTypeFlag++;
			}else if(imageTypeFlag ==1)
				manipulatedBuffImg=revertToOriginal(pic.getUUID(), Convertors.DISPLAY_THUMB);
			else{
				System.err.println("Error in manipulatePic, mode original.");
			}
			break;
		}
		return manipulatedBuffImg;

	}
	

	private static boolean stringInStringList(String needle,
			final List<String> stringList) {
		boolean keyInList = false;
		for (String key : stringList) {
			if (key.equalsIgnoreCase(needle)) {
				keyInList = true;
				break;
			}
		}
		return keyInList;
	}
	

}
