/*
 * QCalibWindow.cpp
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#include "QCalibWindow.h"
#include "QDrawArea.h"
#include "../core/ColorKeeperModel.h"

#include <QPushButton>
#include <QVBoxLayout>
#include <QFrame>
#include <QPalette>
#include <QDesktopWidget>
#include <QRect>
#include <QMouseEvent>
#include <QWheelEvent>

//TODO remove
#include <iostream>
#include <sstream>

QCalibWindow::QCalibWindow(unsigned int qtScreenIndex) :
	QWidget(NULL, Qt::FramelessWindowHint), _mousePressed(false) {
	setWindowTitle(tr("Calib"));
	setWindowIcon(QIcon("./img/icon.png"));
	_drawArea = new QDrawArea();
	QDesktopWidget qdw;
	QRect rect = qdw.screenGeometry(qtScreenIndex);
	setGeometry(rect);

	/////TODO remove
	std::ostringstream os;
	os << qtScreenIndex << " rect : " << rect.x() << " " << rect.y() << " "
			<< rect.width() << " " << rect.height() << std::endl;
	ColorKeeperModel::logMessage(os.str());
	//////
	QPushButton *closeButton = new QPushButton(tr("Close"));
	closeButton->setMaximumWidth(100);
	closeButton->setMaximumHeight(25);
	connect(closeButton, SIGNAL(clicked()), this, SLOT(hide()));
	QVBoxLayout *layout1 = new QVBoxLayout;
	layout1->addWidget(_drawArea);
	QVBoxLayout *layout2 = new QVBoxLayout;
	layout2->addWidget(closeButton);
#ifdef __APPLE__
	layout2->setAlignment(closeButton, Qt::AlignLeft);
#else
	layout2->setAlignment(closeButton, Qt::AlignHCenter);
#endif
	QVBoxLayout *mainLayout = new QVBoxLayout;
	mainLayout->addLayout(layout1);
	mainLayout->addLayout(layout2);
	setLayout(mainLayout);

	connect(&(ColorKeeperModel::Instance()), SIGNAL(calibWindowZoomChanged()),
			this, SLOT(updateDrawAreaZoom()));
	connect(&(ColorKeeperModel::Instance()), SIGNAL(calibWindowTransChanged()),
			this, SLOT(updateDrawAreaTrans()));
}

void QCalibWindow::showWin() {
	if (isHidden())
		show();
	raise();

}
void QCalibWindow::hideWin() {
	if (!isHidden())
		hide();
}
void QCalibWindow::setMode(QDrawArea::EDrawMode mode) {
	_drawArea->setMode(mode);
}
QDrawArea::EDrawMode QCalibWindow::getMode() {
	return _drawArea->getMode();
}

void QCalibWindow::setGamma(const float & gamma) {
	_drawArea->setGammaColor(ColorKeeperModel::getBGColorFromGamma(gamma));
}
void QCalibWindow::setImagePath(const QString& path) {
	_drawArea->setImagePath(path);
}

void QCalibWindow::setPatch(const float &r, const float &g, const float &b,
		const bool &halo) {
	_drawArea->setPatchColor(r, g, b);
	_drawArea->setDrawHalo(halo);
	_drawArea->setMode(QDrawArea::PATCH);
}

void QCalibWindow::setFullScreenRectangle(const float &r, const float &g,
		const float &b) {
	_drawArea->setPatchColor(r, g, b);
	_drawArea->setMode(QDrawArea::SCREEN_REC);
}
void QCalibWindow::setMosaicEnable(const bool &showMosaic) {
	_drawArea->setMosaicEnable(showMosaic);
}

QCalibWindow::~QCalibWindow() {
	//	if (_drawArea != NULL)
	//		delete _drawArea;
}

void QCalibWindow::mousePressEvent(QMouseEvent *event) {
	_lastMouseX = event->x();
	_lastMouseY = event->y();
	_mousePressed = true;
}

void QCalibWindow::mouseReleaseEvent(QMouseEvent *event) {
	_mousePressed = false;
}

void QCalibWindow::mouseMoveEvent(QMouseEvent * event) {
	if (_mousePressed) {
		int currentMouseX = event->x();
		int currentMouseY = event->y();
		ColorKeeperModel& model = ColorKeeperModel::Instance();
		int currentTransX = model.getCalibWindowsTranslateX();
		int currentTransY = model.getCalibWindowsTranslateY();
		model.setCalibWindowsTranslate(-_lastMouseX + currentMouseX
				+ currentTransX, -_lastMouseY + currentMouseY + currentTransY);
		_lastMouseX = currentMouseX;
		_lastMouseY = currentMouseY;
	}
}

void QCalibWindow::mouseDoubleClickEvent(QMouseEvent * event) {
	ColorKeeperModel& model = ColorKeeperModel::Instance();
	model.setCalibWindowsZoom(1);
	model.setCalibWindowsTranslate(0, 0);
}

void QCalibWindow::wheelEvent(QWheelEvent *event) {
	ColorKeeperModel& model = ColorKeeperModel::Instance();
	float zoom = model.getCalibWindowsZoom() + (event->delta() / 360.0f) / 3.f;
	model.setCalibWindowsZoom(zoom);
}

void QCalibWindow::keyReleaseEvent(QKeyEvent * event) {

	if(event->key()!=Qt::Key_F11)
		return;

	Qt::WindowFlags flags = windowFlags();
	if (flags.testFlag(Qt::FramelessWindowHint)) {
		flags &= ~Qt::FramelessWindowHint;
		setWindowFlags(flags);
		showMaximized();
	} else {
		flags |= Qt::FramelessWindowHint;
		setWindowFlags(flags);
		showFullScreen();
	}
}

void QCalibWindow::updateDrawAreaZoom() {
	_drawArea->setZoom(ColorKeeperModel::Instance().getCalibWindowsZoom());
}
void QCalibWindow::updateDrawAreaTrans() {
	_drawArea->setTranslation(
			ColorKeeperModel::Instance().getCalibWindowsTranslateX(),
			ColorKeeperModel::Instance().getCalibWindowsTranslateY());
}

