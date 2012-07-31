package fr.hd3d.colortribe.color.recycle;


public interface IColorConvertibleSetProvider extends Cloneable {
    IColorConvertibleSet getColorConvertibleSet();

    public IColorConvertibleSetProvider clone();
}
