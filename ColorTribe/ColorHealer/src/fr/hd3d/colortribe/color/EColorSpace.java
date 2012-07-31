package fr.hd3d.colortribe.color;



/**
 * Defines a set of standard color spaces
 * As this set is an enum you can get a list of available items.
 * @author Guillaume CHATELET
 */
public enum EColorSpace {
    UNDEFINED("Color space is undefined"), // You can add descriptions to color spaces
    ANY, //
    RGB, //
    YUV, //
    CIExyY, //
    CIEXYZ, //
    CIExyz, //
    CIELab, //
    CIELabNHU, //
    CIELuv, //
    CIELCH, //
    CIEUVW, //
    CIEuvw, //
    DENSITYNEG, //
    DENSITYPOS; //
    private final String description;

    private EColorSpace(String description) {
        this.description = description;
    }

    private EColorSpace() {
        this.description = "";
    }

    public String getDescription() {
        return description;
    }
}
