/*
 * QMiniToyWindow.h
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#ifndef QMINITOYWINDOW_H_
#define QMINITOYWINDOW_H_

#include <QWidget>
#include <QPoint>

class QTabWidget;
class QDisplayDevicePanel;
class QMouseEvent;

class QMiniToyWindow : public QWidget {
	Q_OBJECT
public:
	QMiniToyWindow();
	virtual ~QMiniToyWindow();
	void showWin();
	void hideWin();
private:
	QTabWidget * tabWidget;
	QDisplayDevicePanel *dispDevPan;
	QPoint m_Diff;

	void mousePressEvent  (QMouseEvent *event);
	void mouseReleaseEvent(QMouseEvent *event);
	void mouseMoveEvent   (QMouseEvent *event);
};

#endif /* QMINITOYWINDOW_H_ */
