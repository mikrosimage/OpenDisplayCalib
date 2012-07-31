/*
 * QMiniToyWindow.cpp
 *
 *  Created on: 20 juil. 2009
 *      Author: mfe
 */

#include "QMiniToyWindow.h"
#include "GuiSizeDef.h"
#include "QDisplayDevicesPanel.h"
#include "QMiniGammaTab.h"
#include "QMiniPreviewTab.h"

#include <QIcon>
#include <QHBoxLayout>
#include <QCursor>
#include <QMouseEvent>
#include <QTabWidget>

QMiniToyWindow::QMiniToyWindow(): QWidget(NULL) {//, Qt::FramelessWindowHint
	resize(QSize(MINITOY_WIDTH, MINITOY_HEIGHT));
	setFixedSize(MINITOY_WIDTH, MINITOY_HEIGHT);
	setWindowTitle(tr("MiniToy"));
	setWindowIcon(QIcon("./img/icon.png"));

	QHBoxLayout *mainLayout = new QHBoxLayout;
	dispDevPan = new QDisplayDevicePanel(this,
			MINI_DISPDEVPAN_WIDTH, MINI_DISPDEVPAN_HEIGHT,
			MINI_DISPDEVICON_SIZE, 1);

	mainLayout->addWidget(dispDevPan);
	mainLayout->setAlignment(dispDevPan, Qt::AlignTop | Qt::AlignHCenter);
	move(1, 1);

	tabWidget = new QTabWidget();
	tabWidget->resize(QSize(MINI_COLORTAB_WIDTH, MINI_COLORTAB_HEIGHT));
	tabWidget->setFixedSize(MINI_COLORTAB_WIDTH, MINI_COLORTAB_HEIGHT);
	tabWidget->addTab(new QMiniGammaTab(dispDevPan, tabWidget), tr("Gamma"));
	tabWidget->addTab(new QMiniPreviewTab(dispDevPan, tabWidget), tr("Preview"));

	mainLayout->addWidget(tabWidget);
	//mainLayout->setAlignment(tabWidget, Qt::AlignHCenter | Qt::AlignTop);
	setLayout(mainLayout);
}

void QMiniToyWindow::showWin() {
	if (isHidden())
		show();
	raise();

}

void QMiniToyWindow::hideWin() {
	if (!isHidden())
		hide();
}

QMiniToyWindow::~QMiniToyWindow() {
	// TODO Auto-generated destructor stub
}

void QMiniToyWindow::mousePressEvent(QMouseEvent *event)
{
  m_Diff = event->pos();

  setCursor(QCursor(Qt::SizeAllCursor));
}
void QMiniToyWindow::mouseReleaseEvent(QMouseEvent *event)
{
  Q_UNUSED(event);

  setCursor(QCursor(Qt::ArrowCursor));
}
void QMiniToyWindow::mouseMoveEvent(QMouseEvent *event)
{
  QPoint p = event->globalPos();

  this->move(p - m_Diff);
}
