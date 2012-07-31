/*
 * QManagedApplication.h
 *
 *  Created on: 22 dec. 2011
 *      Author: mfe
 */

#ifndef QCUSTOMAPPLICATION_H_
#define QCUSTOMAPPLICATION_H_

#include <QApplication>

class QManagedApplication: public QApplication {
Q_OBJECT
public:
	QManagedApplication(int &argc, char **argv);
	int exec();
protected:
	void commitData(QSessionManager & manager);
};

#endif /* QCUSTOMAPPLICATION_H_ */
