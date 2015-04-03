/*
 * QDrawArea.cpp
 *
 *  Created on: 18 mars 2009
 *      Author: mfe
 */

#include "QDrawArea.h"
#include "../core/ColorKeeperModel.h"
#include <cmath>
#include <iostream>
#include <sstream>

const double QDrawArea::PI = 3.14159265;

QDrawArea::QDrawArea() :
	QGLWidget(), _width(0), _height(0), _mode(NOTH), _gammaColor(0.0f),
			_imagePath(""), _mosaicEnable(false), _r(1), _g(1), _b(1),
			_drawHalo(true), _currentImageTexture(NULL), _zoom(1.0f),_translateX(0), _translateY(0) {
	_mustRebind = false;

}

QDrawArea::~QDrawArea() {
}

void QDrawArea::setMode(EDrawMode mode) {
	_mode = mode;
	update();
}
void QDrawArea::setGammaColor(const float & gamma) {
	_gammaColor = gamma;
}

void QDrawArea::setMosaicEnable(const bool &b) {
	_mosaicEnable = b;
}

void QDrawArea::setImagePath(const QString &path) {
	_imagePath = path;
	_mustRebind = true;
}

void QDrawArea::setPatchColor(const float & r, const float & g, const float & b) {
	_r = r;
	_g = g;
	_b = b;
}

void QDrawArea::initializeGL() {
	// Set up the rendering context, define display lists etc.:
	glClearColor(0.f, 0.f, 0.f, 0.f);
	setFocusPolicy(Qt::StrongFocus);
}

void QDrawArea::drawGrayRamp() {
	glBegin( GL_QUADS);
	glColor3d(0, 0, 0);
	glVertex2d(0, 0);
	glVertex2d(0, _height / 2);
	glColor3d(1, 1, 1);
	glVertex2d(_width, _height / 2);
	glVertex2d(_width, 0);
	glColor3d(1, 1, 1);
	glVertex2d(0, _height);
	glVertex2d(0, _height / 2);
	glColor3d(0, 0, 0);
	glVertex2d(_width, _height / 2);
	glVertex2d(_width, _height);
	glEnd();
}

void QDrawArea::drawImage(const QString &) {
	if (_mustRebind) {
		std::map<QString, ImageTexture>::iterator it = _imagesTextures.find(
				_imagePath);
		if (it == _imagesTextures.end()) {
			QPixmap pixmap(_imagePath);
			int texId = bindTexture(pixmap, GL_TEXTURE_2D);
			int imgWidth = pixmap.width();
			int imgHeight = pixmap.height();
			ImageTexture texture(imgWidth, imgHeight, texId);
			_imagesTextures.insert(std::make_pair(_imagePath, texture));
			it = _imagesTextures.find(_imagePath);
		}
		_currentImageTexture = &((*it).second);

		_mustRebind = false;
	}

	int currentWidth = _currentImageTexture->_imgWidth;
	if (currentWidth > _width)
		currentWidth = _width;
	int currentHeight = _currentImageTexture->_imgHeight;
	if (currentHeight > _height)
		currentHeight = _height;

	int xOffset = (_width - currentWidth) / 2;
	int yOffset = (_height - currentHeight) / 2;

	QRectF rect(0.0f + xOffset, 0.0f + yOffset, currentWidth, currentHeight);
	glColor3f(1.f, 1.f, 1.f);
	glBegin( GL_QUADS);
	glVertex2d(0 + xOffset, 0 + yOffset);
	glVertex2d(0 + xOffset, currentHeight + yOffset);
	glVertex2d(currentWidth + xOffset, currentHeight + yOffset);
	glVertex2d(currentWidth + xOffset, 0 + yOffset);
	glEnd();

	drawTexture(rect, _currentImageTexture->_texId, GL_TEXTURE_2D);

}
//double raddeg(double x, double y, double theta) {
//	theta *= 180.0 / QDrawArea::PI;
//	if (x > 0) {
//		if (y > 0)
//			theta = theta + 0;
//		else
//			theta = theta + 360;
//	} else {
//		if (y > 0)
//			theta = 180 + theta;
//		else
//			theta = 180 + theta;
//	}
//	if (theta > 360) {
//		theta -= 360;
//	}
//
//	return theta;
//}
//
//float fastAtan2(float y, float x) {
//
//	static float HALF_PI = QDrawArea::PI / 2;
//	static float DOUBLE_PI = QDrawArea::PI * 2;
//	static int atanPrecision = 4096;
//
//	float *halfQuadAtan = new float[atanPrecision + 1];
//	int i = 0;
//	float z;
//	while (i <= atanPrecision) {
//		z= (i + .5f) / atanPrecision;
//		halfQuadAtan[i] = (float) atan(z);
//		i++;
//	}
//
//	// we find in which half quadrant the angle is.
//	if (y >= 0) // upper
//	{
//		if (x >= 0) // upper right
//		{
//			if (y <= x)
//				return halfQuadAtan[(int) (y / x * atanPrecision)];
//			else
//				return HALF_PI - halfQuadAtan[(int) (x / y * atanPrecision)];
//		} else
//		// upper left
//		{
//			x = -x;
//			if (y >= x)
//				return HALF_PI + halfQuadAtan[(int) (x / y * atanPrecision)];
//			else
//				return QDrawArea::PI - halfQuadAtan[(int) (y / x * atanPrecision)];
//		}
//	} else
//	// lower
//	{
//		y = -y;
//		if (x >= 0) // lower right
//		{
//			if (y <= x)
//				return QDrawArea::PI + QDrawArea::PI - halfQuadAtan[(int) (y / x * atanPrecision)];
//			else
//				return QDrawArea::PI + HALF_PI
//						+ halfQuadAtan[(int) (x / y * atanPrecision)];
//		} else
//		// upper left
//		{
//			x = -x;
//			if (y >= x)
//				return QDrawArea::PI + HALF_PI
//						- halfQuadAtan[(int) (x / y * atanPrecision)];
//			else
//				return QDrawArea::PI + halfQuadAtan[(int) (y / x * atanPrecision)];
//		}
//	}
//}

void QDrawArea::drawPatch(const float & r, const float & g, const float & b) {
	glColor3f(_gammaColor, _gammaColor, _gammaColor);
	glBegin( GL_QUADS);
	glVertex2d(0, 0);
	glVertex2d(0, _height);
	glVertex2d(_width, _height);
	glVertex2d(_width, 0);
	glEnd();

	// patch
	int xOffset = (_width) / 2;
	int yOffset = (_height) / 2;

	int size = _height * 20 / 100;

	GLint i;
	static GLfloat circoords[100][2];
	static GLint inited = 0;

	if (inited == 0) {
		inited = 1;
		for (i = 0; i < 100; i++) {
			circoords[i][0] = size / 2 * cos(i * 2 * PI / 100.0) + xOffset;
			circoords[i][1] = size / 2 * sin(i * 2 * PI / 100.0) + yOffset;
		}
	}
	glColor3f(r, g, b);
	glBegin( GL_POLYGON);
	glBegin(GL_POLYGON);
	for (i = 0; i < 100; i++)
		glVertex2fv(&circoords[i][0]);
	glEnd();
	glEnd();

	//	//test radial gradient
	//	std::ostringstream os;
	//	float theta;
	//	glBegin(GL_POLYGON);
	//	for (i = -50; i < 50; i++) {
	//		float x = cos(i * 2 * PI / 100.0);
	//		float y = sin(i * 2 * PI / 100.0);
	//		theta =fastAtan2(y, x)/(2*PI);// raddeg(x, y, atan(y / x)) / 360;
	//		os << theta << std::endl;
	//		ColorKeeperModel::logMessage(os.str());
	//		glColor3f(theta, theta, theta);
	//		glVertex2fv(&circoords[i+50][0]);
	//	}
	//
	//	glEnd();

	// halo (pr placer la sonde)
	glColor3f(r, g, b);
	if (_drawHalo) {

		size = _height * 30 / 100;
		static GLfloat circoordsHalo[100][2];
		static GLint initedHalo = 0;

		if (initedHalo == 0) {
			initedHalo = 1;
			for (i = 0; i < 100; i++) {
				circoordsHalo[i][0] = size / 2 * cos(i * 2 * PI / 100.0)
						+ xOffset;
				circoordsHalo[i][1] = size / 2 * sin(i * 2 * PI / 100.0)
						+ yOffset;
			}
		}
		glBegin( GL_LINE_LOOP);
		for (i = 0; i < 100; i++)
			glVertex2fv(&circoordsHalo[i][0]);
		glEnd();
	}

	xOffset = (_width);
	yOffset = (_height);
	//indicateur de couleur en bas à droite
	size = 20;
	static GLfloat circoordsIndic[100][2];
	static GLint initedIndic = 0;
	if (initedIndic == 0) {
		initedIndic = 1;
		for (i = 0; i < 100; i++) {
			circoordsIndic[i][0] = size / 2 * cos(i * 2 * PI / 100.0) + xOffset;
			circoordsIndic[i][1] = size / 2 * sin(i * 2 * PI / 100.0) + yOffset;
		}
	}
	glBegin(GL_POLYGON);
	for (i = 0; i < 100; i++)
		glVertex2fv(&circoordsIndic[i][0]);
	glEnd();

	//	glBegin(GL_POLYGON);
	//	for (float angle = 0; angle <= 2 * PI; angle += PI / step) {
	//		float x = size * cos(angle) / 2 + xOffset;
	//		float y = size * sin(angle) / 2 + yOffset;
	//
	//		glVertex2f(x, y);
	//		x = size * cos(angle + PI / step) / 2 + xOffset;
	//		y = size * sin(angle + PI / step) / 2 + yOffset;
	//		glVertex2f(x, y);
	//	}
	//	glEnd();

}
void QDrawArea::drawFullScreenRectangle(const float & r, const float & g,
		const float & b) {
	glColor3f(r, g, b);
	glBegin( GL_QUADS);
	glVertex2d(0, 0);
	glVertex2d(0, _height);
	glVertex2d(_width, _height);
	glVertex2d(_width, 0);
	glEnd();
}

void QDrawArea::drawLumContPattern(const int & recW, const int & recH,
		const int & bandH, const int & interBand, const int & mosOffsetX,
		const int & mosOffsetY) {

	// lum pattern
	float backValue = 8 / 255.f;// 8 (8bits)
	glColor3f(backValue, backValue, backValue);
	int xOffset = (_width - recW) / 2 + mosOffsetX;
	int yOffset = (_height - (recH + bandH + interBand)) / 2 + mosOffsetY;
	glBegin( GL_QUADS);
	glVertex2d(xOffset, yOffset);
	glVertex2d(xOffset, yOffset + recH);
	glVertex2d(xOffset + recW, yOffset + recH);
	glVertex2d(xOffset + recW, yOffset);
	glEnd();

	float leftPatch = 16 / 255.f;// 16 (8bits)
	glColor3f(leftPatch, leftPatch, leftPatch);
	int square = 90;
	int interW = (recW - square * 2) / 3;
	int interH = (recH - square) / 2;

	glBegin(GL_QUADS);
	glVertex2d(xOffset + interW, yOffset + interH);
	glVertex2d(xOffset + interW, yOffset + interH + square);
	glVertex2d(xOffset + interW + square, yOffset + interH + square);
	glVertex2d(xOffset + interW + square, yOffset + interH);
	glEnd();

	float rightPatch = 0.f;
	glColor3f(rightPatch, rightPatch, rightPatch);

	glBegin(GL_QUADS);
	glVertex2d(xOffset + interW * 2 + square, yOffset + interH);
	glVertex2d(xOffset + interW * 2 + square, yOffset + interH + square);
	glVertex2d(xOffset + interW * 2 + square * 2, yOffset + interH + square);
	glVertex2d(xOffset + interW * 2 + square * 2, yOffset + interH);
	glEnd();

	//cont pattern
	int patchW = (int) (400 / 13.f + 0.5f);
	int patchH = bandH;
	//	255
	//	240
	//	226
	//	197
	//	170
	//	143
	//	118
	//	94
	//	72
	//	52
	//	33
	//	24
	//	0

	float colorValue[] = { 0 / 255.f, 24 / 255.f, 33 / 255.f, 52 / 255.f, 72
			/ 255.f, 94 / 255.f, 118 / 255.f, 143 / 255.f, 170 / 255.f, 197
			/ 255.f, 226 / 255.f, 240 / 255.f, 255 / 255.f };
	for (int i = 0; i < 13; i++) {
		glBegin(GL_QUADS);
		glColor3f(colorValue[12 - i], colorValue[12 - i], colorValue[12 - i]);
		glVertex2d(xOffset + i * patchW, yOffset + (recH + interBand));
		glVertex2d(xOffset + i * patchW, yOffset + (recH + interBand) + patchH);
		glVertex2d(xOffset + i * patchW + patchW, yOffset + (recH + interBand)
				+ patchH);
		glVertex2d(xOffset + i * patchW + patchW, yOffset + (recH + interBand));
		glEnd();
	}
}

void QDrawArea::drawLumContPattern() {
	// background
	glColor3f(_gammaColor, _gammaColor, _gammaColor);
	glBegin( GL_QUADS);
	glVertex2d(0, 0);
	glVertex2d(0, _height);
	glVertex2d(_width, _height);
	glVertex2d(_width, 0);
	glEnd();
	int margin = 10;
	int recW = 400;
	int recWMarg = recW + margin;
	int recH = 150;
	int bandH = 30;
	int interBand = 10;

	if (_mosaicEnable) {

		int mult = _width / recW;

		if (_height / (recH + bandH + interBand) > mult)
			mult = _height / (recH + bandH + interBand);
		for (int i = 0; i <= mult; i++) {
			for (int j = i; j <= mult; j++) {
				drawLumContPattern(recW, recH, bandH, interBand, j * recWMarg,
						i * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -j * recWMarg,
						i * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, i * recWMarg,
						-j * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -i * recWMarg,
						-j * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -i * recWMarg,
						-j * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, i * recWMarg,
						j * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -i * recWMarg,
						j * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, j * recWMarg,
						-i * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -j * recWMarg,
						-i * (recH + bandH + interBand + margin));
				drawLumContPattern(recW, recH, bandH, interBand, -j * recWMarg,
						-i * (recH + bandH + interBand + margin));
			}
		}
	} else {
		drawLumContPattern(recW, recH, bandH, interBand, 0, 0);
	}

}

void QDrawArea::resizeGL(int w, int h) {
	if (w == 0 || h == 0)
		return;

	_width = w;
	_height = h;

	// Changement de repere
	// setup viewport, projection etc.:
	glViewport(0, 0, _width - 1, _height - 1);

	glMatrixMode( GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0.5f, _width - 0.5f, _height - 0.5f, 0.5f, -1.0, 1.0);

	glMatrixMode( GL_MODELVIEW);
	glLoadIdentity();
}

void QDrawArea::paintGL() {

	glClearColor(0.f, 0.f, 0.f, 0.f);
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	glMatrixMode( GL_MODELVIEW);
	glLoadIdentity();
	switch (_mode) {
	case NOTH:
		break;
	case GRAYRAMP:
		drawGrayRamp();
		break;
	case IMAGE:
		glTranslatef(_width/2, _height/2, 0.0f);
		glScalef(_zoom, _zoom, 1.0f);
		glTranslatef(-_width/2, -_height/2, 0.0f);
		glTranslatef(_translateX, _translateY, 0.0f);
		drawImage(_imagePath);
		break;
	case LUMCONTPATT:
		drawLumContPattern();
		break;
	case PATCH:
		drawPatch(_r, _g, _b);
		break;
	case SCREEN_REC:
		drawFullScreenRectangle(_r, _g, _b);
		break;
	default:
		break;
	}
}
