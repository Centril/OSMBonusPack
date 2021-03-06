package org.osmdroid.bonuspack.kml;

import java.io.IOException;
import java.io.Writer;

import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Handling of KML Style (PolyStyle, LineStyle, IconStyle)
 * @author M.Kergall
 */
public class Style implements Parcelable {

	public ColorStyle outlineColorStyle;
	public ColorStyle fillColorStyle;
	public ColorStyle iconColorStyle;
	public float outlineWidth = 0.0f;
	public String iconHref;
	//TODO iconScale
	
	/** default constructor */
	Style(){
	}
	
	/** 
	 * @return a Paint corresponding to the style (for a line or a polygon outline)
	 */
	public Paint getOutlinePaint(){
		Paint outlinePaint = new Paint();
		if (outlineColorStyle != null)
			outlinePaint.setColor(outlineColorStyle.getFinalColor());
		outlinePaint.setStrokeWidth(outlineWidth);
		outlinePaint.setStyle(Paint.Style.STROKE);
		return outlinePaint;
	}
	
	protected void writeOneStyle(Writer writer, String styleType, ColorStyle colorStyle){
		try {
			writer.write("<"+styleType+">\n");
			colorStyle.writeAsKML(writer);
			//write the specifics:
			if (styleType.equals("LineStyle")){
				writer.write("<width>"+outlineWidth+"</width>\n");
			} else if (styleType.equals("IconStyle")){
				if (iconHref != null)
					writer.write("<Icon><href>"+iconHref+"</href></Icon>\n");
			}
		writer.write("</"+styleType+">\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeAsKML(Writer writer, String styleId){
		try {
			writer.write("<Style id=\'"+styleId+"\'>\n");
			if (outlineColorStyle != null)
				writeOneStyle(writer, "LineStyle", outlineColorStyle);
			if (fillColorStyle != null)
				writeOneStyle(writer, "PolyStyle", fillColorStyle);
			if (iconColorStyle != null)
				writeOneStyle(writer, "IconStyle", iconColorStyle);
			writer.write("</Style>\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//Parcelable implementation ------------
	
	@Override public int describeContents() {
		return 0;
	}

	@Override public void writeToParcel(Parcel out, int flags) {
		out.writeParcelable(outlineColorStyle, flags);
		out.writeParcelable(fillColorStyle, flags);
		out.writeParcelable(iconColorStyle, flags);
		out.writeFloat(outlineWidth);
		out.writeString(iconHref);
	}
	
	public static final Parcelable.Creator<Style> CREATOR = new Parcelable.Creator<Style>() {
		@Override public Style createFromParcel(Parcel source) {
			return new Style(source);
		}
		@Override public Style[] newArray(int size) {
			return new Style[size];
		}
	};
	
	public Style(Parcel in){
		outlineColorStyle = in.readParcelable(Style.class.getClassLoader());
		fillColorStyle = in.readParcelable(Style.class.getClassLoader());
		iconColorStyle = in.readParcelable(Style.class.getClassLoader());
		outlineWidth = in.readFloat();
		iconHref = in.readString();
	}
}
