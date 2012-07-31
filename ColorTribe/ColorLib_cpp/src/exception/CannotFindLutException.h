#ifndef COLORRAMDACLUTEXCEPTION_H_
#define COLORRAMDACLUTEXCEPTION_H_

#include<iostream>
#include "ColorRamDacException.h"

class CannotFindLutException : public ColorRamDacException
{
	std::string _message;
public:
	CannotFindLutException(const std::string path);
	virtual ~CannotFindLutException() throw ();
	virtual const std::string what() const throw();
};

#endif /*COLORRAMDACLUTEXCEPTION_H_*/
