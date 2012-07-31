/*
 * QMiniGammaTab.h
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#ifndef QMiniPreviewTab_H_
#define QMiniPreviewTab_H_

#include <QWidget>

class QDisplayDevicePanel;
class QStackedWidget;
class QListWidgetItem;

class QMiniPreviewTab : public QWidget {
Q_OBJECT
public:
	QMiniPreviewTab(QDisplayDevicePanel * dispDevPan, QWidget *parent = 0);
	virtual ~QMiniPreviewTab();

	QDisplayDevicePanel * _dispDevPan;
	QStackedWidget *_pagesWidget;

	void createPages();

private slots:
void changePage(QListWidgetItem *current, QListWidgetItem *previous);

};

#endif /* QMiniPreviewTab_H_ */
