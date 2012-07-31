/*
 * QLutDisplay.cpp
 *
 *  Created on: 20 mars 2009
 *      Author: mfe
 */

#include "QLutDisplay.h"

#include "../core/ColorKeeperModel.h"

QLutDisplay::QLutDisplay(const LUT2DCorrection * lut, const unsigned int screenIndex) : QGLWidget(),
	 _lut(lut), _width(0), _height(0), _OSScreenIndex(screenIndex) {
	connect(&(ColorKeeperModel::Instance()), SIGNAL(correctionChanged(unsigned int)), this, SLOT(lutRepaint(unsigned int)));

}

void QLutDisplay::initializeGL() {
	glClearColor(0.f, 0.f, 0.f, 0.f);
	setFocusPolicy(Qt::StrongFocus);
}

void QLutDisplay::resizeGL(int w, int h) {
	if (w == 0 || h == 0)
		return;

	_width = w;
	_height = h;

	glViewport(0, 0, _width - 1, _height - 1);

	glMatrixMode(GL_PROJECTION);
	glLoadIdentity();
	glOrtho(0.5f, _width - 0.5f, 0.5f, _height - 0.5f, -1.0, 1.0);

	glMatrixMode(GL_MODELVIEW);
	glLoadIdentity();
}

void QLutDisplay::drawLUT() {
	bool isCorrectionEnable =
			ColorKeeperModel::Instance().shouldApplyCorrection(_OSScreenIndex);

	glColor3f(0.5f, 0.5f, 0.5f);
	unsigned short* lut = _lut->get16BitsLUT();
	glLineWidth(1);
	if (isCorrectionEnable)
		glColor3f(1.f, 1.f, 1.f);
	glBegin(GL_LINE_STRIP);
	glVertex2d(0.f, 0.f);
	glVertex2d(_width, _height);
	glEnd();
	if (isCorrectionEnable)
		glColor3f(1.f, 0.f, 0.f);
	glBegin(GL_LINE_STRIP);
	unsigned int rampSize = _lut->getRampSize();
	for (unsigned int i = 0; i < rampSize; i++) {
		float x = (float) _width / (float) (rampSize - 1) * i;
		float y = (float) _height / 65535.f * lut[i * 3];
		glVertex2d(x, y);
	}
	glEnd();
	if (isCorrectionEnable)
		glColor3f(0.f, 1.f, 0.f);
	glBegin(GL_LINE_STRIP);
	for (unsigned int i = 0; i < rampSize; i++) {
		float x = (float) _width / (float) (rampSize - 1) * i;
		float y = (float) _height / 65535.f * lut[i * 3 + 1];
		glVertex2d(x, y);
	}
	glEnd();
	if (isCorrectionEnable)
		glColor3f(0.f, 0.f, 1.f);
	glBegin(GL_LINE_STRIP);
	for (unsigned int i = 0; i < rampSize; i++) {
		float x = (float) _width / (float) (rampSize - 1) * i;
		float y = (float) _height / 65535.f * lut[i * 3 + 2];
		glVertex2d(x, y);
	}
	glEnd();
	delete lut;

	if (!isCorrectionEnable){
		glColor3f(1.f, 0.f, 0.f);
		renderText( 50, _height/2, "/!\\ Correction disable");
	}

}

void QLutDisplay::lutRepaint(unsigned int index) {
	if (index == _OSScreenIndex) {
		const LUT2DCorrection * custom = ColorKeeperModel::Instance().getSelectedCustomLut(_OSScreenIndex);
		if(custom != NULL)
			_lut = custom;
		else _lut = &ColorKeeperModel::Instance().getScreenProfil(_OSScreenIndex);
		update();
	}
}

void QLutDisplay::paintGL() {
	glClear(GL_COLOR_BUFFER_BIT);
	drawLUT();
}

QLutDisplay::~QLutDisplay() {
}
