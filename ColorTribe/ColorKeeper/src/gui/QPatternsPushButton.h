/*
 * QPatternsPushButton.h
 *
 *  Created on: 31 mai 2010
 *      Author: mfe
 */

#ifndef QPATTERNSPUSHBUTTON_H_
#define QPATTERNSPUSHBUTTON_H_
#include <QPushButton>
class QCalibWindow;
class QString;
class QIcon;
class QPatternsPushButton: public QPushButton {
Q_OBJECT

public:
	QPatternsPushButton(QCalibWindow * calibWindow, QIcon icon,QString path);
	virtual ~QPatternsPushButton();
private:
	QCalibWindow * _refCalibWindow;
	QString _path;
	void mousePressEvent(QMouseEvent *event);
};

#endif /* QPATTERNSPUSHBUTTON_H_ */
