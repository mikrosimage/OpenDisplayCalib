/********************************************************************
  filename:  EyeOneException.java
	
  purpose:	 Exception thrown by the EyeOne class.

  Copyright (c) 2003 by GretagMacbeth AG Switzerland.
  
  ALL RIGHTS RESERVED.  

  THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS
  OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
  ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
  GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
  WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*********************************************************************/

package com.konicaminolta.cs200;

public class CS200Exception extends Exception {
	/**
     * 
     */
    private static final long serialVersionUID = -428223458889267088L;
    private int code;

	public CS200Exception() {
		super();
		System.out.println("Default Ctor called");
	}
	
	public CS200Exception(String description) {
		super(description);
	}

	public CS200Exception(int code, String description) {
		super(description);
		setCode(code);		
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	public String getMessage() {
		return "error" + getCode() + " : " + super.getMessage();
	}
	
}