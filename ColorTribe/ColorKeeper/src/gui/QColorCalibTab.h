/*
 * QColorCalibTab.h
 *
 *  Created on: 11 mars 2009
 *      Author: mfe
 */

#ifndef QCOLORCALIBTAB_H_
#define QCOLORCALIBTAB_H_

#include <QWidget>

class QDisplayDevicePanel;
class QStackedWidget;
class QListWidgetItem;

class QColorCalibTab: public QWidget {
	Q_OBJECT
public:
	QColorCalibTab(QDisplayDevicePanel * dispDevPan, QWidget *parent = 0);
	QDisplayDevicePanel * _dispDevPan;
	QStackedWidget *_pagesWidget;

	virtual ~QColorCalibTab();

	void createPages();

private slots:
     void changePage(QListWidgetItem *current, QListWidgetItem *previous);

};

#endif /* QCOLORCALIBTAB_H_ */
