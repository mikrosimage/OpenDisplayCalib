/*
 * QColorCronTab.h
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#ifndef QCOLORCRONTAB_H_
#define QCOLORCRONTAB_H_

#include <QWidget>

class QDisplayDevicePanel;
class QStackedWidget;
class QListWidgetItem;

class QColorCronTab: public QWidget {
	Q_OBJECT
public:
	QColorCronTab(QDisplayDevicePanel * dispDevPan, QWidget *parent = 0);
	QDisplayDevicePanel * _dispDevPan;
	QStackedWidget *_pagesWidget;
	virtual ~QColorCronTab();

	void createPages();

private slots:
     void changePage(QListWidgetItem *current, QListWidgetItem *previous);
};

#endif /* QCOLORCRONTAB_H_ */
