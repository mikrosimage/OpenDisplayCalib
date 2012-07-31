/*
 * QDrawArea.h
 *
 *  Created on: 18 mars 2009
 *      Author: mfe
 */

#ifndef QDRAWAREA_H_
#define QDRAWAREA_H_
#include <QGLWidget>
#include <QString>
#include <map>

class ImageTexture;

class QDrawArea: public QGLWidget {
Q_OBJECT
public:
	QDrawArea();
	virtual ~QDrawArea();
	enum EDrawMode {
		NOTH, GRAYRAMP, IMAGE, LUMCONTPATT, PATCH, SCREEN_REC
	};
	static const double PI;
	void setMode(EDrawMode mode);
	EDrawMode getMode() {
		return _mode;
	}
	void setGammaColor(const float &gamma);
	void setImagePath(const QString &path);
	void setMosaicEnable(const bool &b);
	void setPatchColor(const float & r, const float & g, const float & b);
	void setDrawHalo(const bool & drawHalo) {
		_drawHalo = drawHalo;
	}
	void setZoom(float zoom) {

		_zoom = zoom;
		if(_zoom <0)
			_zoom = 0.01;
		update();
	}
	void setTranslation(int transX, int transY){
		_translateX = transX;
		_translateY = transY;
		update();
	}
protected:

	void paintGL();
	void resizeGL(int w, int h);
	void initializeGL();
	void drawGrayRamp();
	void drawImage(const QString &path);
	void drawPatch(const float & r, const float & g, const float & b);
	void drawLumContPattern();
	void drawLumContPattern(const int & recW, const int & recH,
			const int & bandH, const int & interBand, const int & mosOffsetX,
			const int & mosOffsetY);
	void drawFullScreenRectangle(const float & r, const float & g,
			const float & b);
private:
	int _width;
	int _height;
	EDrawMode _mode;
	float _gammaColor;
	QString _imagePath;
	bool _mustRebind;
	bool _mosaicEnable;
	float _r;
	float _g;
	float _b;
	bool _drawHalo;
	ImageTexture* _currentImageTexture;
	std::map<QString, ImageTexture> _imagesTextures;
	float _zoom;
	int _translateX;
	int _translateY;

};

class ImageTexture {
public:
	int _imgWidth;
	int _imgHeight;
	GLuint _texId;
	ImageTexture(int width, int height, GLuint texId) :
		_imgWidth(width), _imgHeight(height), _texId(texId) {
	}
	ImageTexture(const ImageTexture& texture) :
		_imgWidth(texture._imgWidth), _imgHeight(texture._imgHeight), _texId(
				texture._texId) {
	}
};

#endif /* QDRAWAREA_H_ */
