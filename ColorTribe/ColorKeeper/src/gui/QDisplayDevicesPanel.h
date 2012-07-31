/*
 * QDispplayDevicePanel.h
 *
 *  Created on: 9 mars 2009
 *      Author: mfe
 */

#ifndef QDISPPLAYDEVICEPANEL_H_
#define QDISPPLAYDEVICEPANEL_H_

#include <QWidget>
#include <vector>

class QGroupBox;
class QPushButton;
class QListWidget;
class QListWidgetItem;

class QDisplayDevicePanel: public QWidget {
	Q_OBJECT
public:
	QDisplayDevicePanel(QWidget *parent, size_t maxWidth, size_t maxHeight, size_t iconSize, size_t spacing);
	virtual ~QDisplayDevicePanel();
	QListWidget *_contentsWidget;

private slots:
	void correctionStatusChanged(unsigned int);

private:
void createListItem();
bool _isMini;
};

#endif /* QDISPPLAYDEVICEPANEL_H_ */
