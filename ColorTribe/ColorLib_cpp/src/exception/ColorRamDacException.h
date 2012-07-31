#ifndef COLORRAMDACEXCEPTION_H_
#define COLORRAMDACEXCEPTION_H_
#include<iostream>

class ColorRamDacException
{
public:
	ColorRamDacException();
	virtual ~ColorRamDacException() throw ();
	virtual const std::string what() const throw()=0;
};

#endif /*COLORRAMDACEXCEPTION_H_*/
