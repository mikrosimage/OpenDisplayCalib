/*
 * QPatternsPushButton.h
 *
 *  Created on: 31 mai 2010
 *      Author: mfe
 */

#ifndef QPATTERNSWIDGETITEM_H_
#define QPATTERNSWIDGETITEM_H_
#include <QListWidgetItem>
class QCalibWindow;
class QString;
class QIcon;
class QPatternsWidgetItem: public QListWidgetItem {


public:
	QPatternsWidgetItem(QCalibWindow * calibWindow, QIcon icon, QString & path,
			QListWidget *list);
	virtual ~QPatternsWidgetItem();
private:
	QCalibWindow * _refCalibWindow;
	QString _path;
	void mousePressEvent(QMouseEvent *event);
};

#endif /* QPATTERNSWIDGETITEM_H_ */
