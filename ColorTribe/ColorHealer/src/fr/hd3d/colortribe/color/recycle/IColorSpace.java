package fr.hd3d.colortribe.color.recycle;

import fr.hd3d.colortribe.color.EColorSpace;

public interface IColorSpace {
    abstract EColorSpace getColorSpace();

    abstract void setColorSpace(EColorSpace colorSpace);
}
