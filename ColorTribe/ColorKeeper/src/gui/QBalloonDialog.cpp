/*
 * QBalloonDialog.cpp
 *
 *  Created on: 22 oct. 2009
 *      Author: mfe
 */

#include "QBalloonDialog.h"
#include "../core/ColorKeeperModel.h"
#include <QDesktopWidget>
#include <QVBoxLayout>
#include <QLabel>
#include <QPushButton>
#include <QPainter>
#include <QPixmap>
#include <QImage>
#include <QBitmap>
#include <QDesktopServices>
#include <QUrl>

//to remove
#include <iostream>
#include <sstream>

QBalloonDialog::QBalloonDialog(QString title, QString message, int posX,
		int posY, bool weAreInTrouble) :
	QDialog(NULL, Qt::FramelessWindowHint) {
	resize(QSize(250, 180));
	setWindowTitle(title);
	setWindowIcon(QIcon("./img/icon.png"));
	std::ostringstream os;
	os << "position Systray " << posX << " " << posY << std::endl;
	ColorKeeperModel::logMessage(os.str());
#ifdef __APPLE__
	move(0, 0);
#else

	QDesktopWidget qdw;
	int screenCount = qdw.numScreens();//screenCount() --> 4.6
	QRect rectFinal = qdw.screenGeometry(0);
	for (int i = 1; i < screenCount; i++) {
		rectFinal = rectFinal.united(qdw.screenGeometry(i));
	}
	int x, y, width, height;
	rectFinal.getRect(&x, &y, &width, &height);
	std::ostringstream os2;
	int x2 = x + width;
	int y2 = y + height;
	os2 << "rect Final  " << x << " " << y << " " << x2 << " " << y2
			<< std::endl;
	int newPosX = posX - 250;
	int newPosY = posY - 180;
	if ((newPosX > x && newPosX < x2)&&(newPosY > y && newPosY < y2)){
		move(newPosX, newPosY);
		os2 <<"position finale "<<newPosX<<" "<<newPosY<<std::endl;
	}
	else{
		int primScreen = qdw.primaryScreen ();
		rectFinal = qdw.screenGeometry(primScreen);
		newPosX = rectFinal.x() + rectFinal.width() - 255;
		newPosY = rectFinal.y() + rectFinal.height() - 190;
		move(newPosX, newPosY);
		os2 <<"position finale "<<newPosX<<" "<<newPosY<<std::endl;
	}
	ColorKeeperModel::logMessage(os2.str());
#endif
	QHBoxLayout *titleLayout = new QHBoxLayout;
	QLabel* iconLab = new QLabel();
	if (weAreInTrouble) {
		iconLab->setPixmap(QPixmap("./img/pb.png"));
	} else
		iconLab->setPixmap(QPixmap("./img/ok.png"));
	iconLab->setFixedSize(15, 15);
	QLabel* titleLab = new QLabel(title);
	titleLayout->setAlignment(Qt::AlignTop);
	titleLayout->addWidget(iconLab);
	titleLayout->addWidget(titleLab);
	QVBoxLayout *mainLayout = new QVBoxLayout;
	mainLayout->setAlignment(Qt::AlignTop);
	QLabel* messageLab = new QLabel("");
	messageLab->setText(message);
	ColorKeeperModel::logMessage(message.toStdString());
	QLabel
			* linkLab =
					new QLabel(
							"<a href=\"docs/index.html\"><font color='#329BEE'>More infos...</font></a>");
	//linkLab->setOpenExternalLinks(true);
	linkLab->setTextFormat(Qt::RichText);
	connect(linkLab, SIGNAL(
					linkActivated (const QString &)), this, SLOT(openURL(const QString &)));
	QPushButton* closeButton = new QPushButton("ok");
	closeButton->setFixedSize(100, 20);
	mainLayout->addLayout(titleLayout);
	mainLayout->addWidget(messageLab);
	mainLayout->addWidget(linkLab);
	mainLayout->addWidget(closeButton);

	setLayout(mainLayout);
	setAttribute(Qt::WA_DeleteOnClose);
	setAttribute(Qt::WA_TranslucentBackground, true);
	setMask(QBitmap(QPixmap("./img/mask2.png")));

	connect(closeButton, SIGNAL(clicked()), this, SLOT(close()));
	show();
	raise();

}
void QBalloonDialog::openURL(const QString & url) {
	QDesktopServices::openUrl(QUrl(url));
}

void QBalloonDialog::paintEvent(QPaintEvent *) {
	QPainter painter(this);

	QBrush background(QColor(23, 23, 34, 0));

	painter.setBrush(background);
	//painter.setBackgroundMode(Qt::TransparentMode);

	painter.setPen(Qt::NoPen); // No stroke
	//painter.drawRect(0, 0, width(), height());
	QPixmap fond = QPixmap::fromImage(QImage("./img/balloon.png"));
	painter.drawPixmap(0, 0, fond);

}

QBalloonDialog::~QBalloonDialog() {
}

