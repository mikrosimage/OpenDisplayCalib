/*
 * SmtpAuthData.cpp
 *
 *  Created on: Jan 9, 2012
 *      Author: mfe
 */

#include "SmtpAuthData.h"
#include <QDebug>

SmtpAuthData::Data::Data()
{
    enableSsl = false;
}

SmtpAuthData::SmtpAuthData()
{
    id = -1;
}

void SmtpAuthData::rewriteData(const Data &newData)
{
    data = newData;
}

bool SmtpAuthData::isEmpty() const
{
    return (id == -1) ? true
        : false ;
}
