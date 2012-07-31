/*
 * QCalibWindow.h
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#ifndef QCALIBWINDOW_H_
#define QCALIBWINDOW_H_
#include "QDrawArea.h"
#include <QWidget>

class QDrawArea;
class QMouseEvent;

class QCalibWindow: public QWidget {
	Q_OBJECT
public:
	QCalibWindow(unsigned int screenIndex);
	virtual ~QCalibWindow();

	void showWin();
	void hideWin();
	void setMode(QDrawArea::EDrawMode  mode);
	QDrawArea::EDrawMode getMode();
	void setGamma(const float &gamma);
	void setImagePath(const QString&);
	void setMosaicEnable(const bool &showMosaic);
	void setPatch(const float &r, const float &g, const float &b, const bool &halo);
	void setFullScreenRectangle(const float &r, const float &g, const float &b);
	QDrawArea *_drawArea;

	void mousePressEvent  (QMouseEvent *event);
	void mouseReleaseEvent(QMouseEvent *event);
	void  mouseMoveEvent ( QMouseEvent * event );
	 void	mouseDoubleClickEvent ( QMouseEvent * event );
	void wheelEvent(QWheelEvent *event);
	void keyReleaseEvent ( QKeyEvent * event );
private:
	int _lastMouseX;
	int _lastMouseY;
	bool _mousePressed;
private slots:
void updateDrawAreaZoom();
void updateDrawAreaTrans();

};

#endif /* QCALIBWINDOW_H_ */
