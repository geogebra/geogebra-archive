package yacas;


class InputStatus
{
  public InputStatus()
	{ 
	  iFileName = "none";
	  iLineNumber = -1;
	}

  public InputStatus(InputStatus aPreviousStatus)
	{
	  iFileName = aPreviousStatus.iFileName;
	  iLineNumber = aPreviousStatus.iLineNumber;
//Application.debug("InputStatus construct to "+iFileName);
	}
  public void SetTo(String aFileName)
	{
//Application.debug("InputStatus set to "+aFileName);
	  iFileName = aFileName;
    iLineNumber = 1;
	}
  public void RestoreFrom(InputStatus aPreviousStatus)
	{
	  iFileName = aPreviousStatus.iFileName;
	  iLineNumber = aPreviousStatus.iLineNumber;

//Application.debug("InputStatus restore to "+iFileName);

	}
  public int LineNumber()
	{
		return iLineNumber;
	}
  public String FileName()
	{
		return iFileName;
	}
  public  void NextLine()
	{
		iLineNumber++;
	}
  String iFileName;
  int	iLineNumber;
};

