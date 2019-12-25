package com.DevStoreDemo;

public class PlayBoard {

	private int[][] playArea;
		
	public PlayBoard(int boardLength, int boardHeigth)
	{
		playArea = new int[boardLength][boardHeigth];
		//Initialize playArea.
		for(int i = 0; i < playArea.length; i++)
			for(int j = 0; j < playArea[i].length; j++)
				playArea[i][j] = 0;
	}
	
	public int getBoardValue(int x, int y)
	{
		return playArea[x][y];
	}
	
	
	// It means the fragment can't be put into the play board if return false.
	public boolean PutFragment(Fragment fragment)
	{
		for(int j = 0; j < fragment.getLength(); j++)
		{
			for(int k = 0; k < fragment.getHeight(); k++)
			{
				if(playArea[fragment.getxPos() + j][fragment.getyPos() + k] == 0)
					playArea[fragment.getxPos() + j][fragment.getyPos() + k] = fragment.getValue();
				else
					return false;
			}	
		}	
		return true;
	}
	
	public boolean isFragmentCanBeMoved(Fragment fragment, int direction)
	{
		switch(direction)
		{
			case Fragment.DIRECTION_UP:
			{
				for (int i = 0; i < fragment.getLength(); i++) 
					if(playArea[fragment.getxPos() + i][fragment.getyPos() - 1] != 0)
						return false;
				break;
			}
			case Fragment.DIRECTION_DOWN:
			{
				for (int i = 0; i < fragment.getLength(); i++) 
					if(playArea[fragment.getxPos() + i][fragment.getyPos() + fragment.getHeight()] != 0)
						return false;
				break;
			}		
			case Fragment.DIRECTION_LEFT:
			{
				for (int i = 0; i < fragment.getHeight(); i++) 
					if(playArea[fragment.getxPos() - 1][fragment.getyPos() + i] != 0)
						return false;
				break;
			}		
			case Fragment.DIRECTION_RIGHT:
			{
				for (int i = 0; i < fragment.getHeight(); i ++)
					if(playArea[fragment.getxPos() + fragment.getLength()][fragment.getyPos() + i] != 0)
						return false;
				break;
			}
		}
		return true;
	}
	
	public void moveFragment(Fragment fragment)
	{
		// Destroy the old fragment.
		for (int i = 0; i < playArea.length; i++)
			for (int j = 0; j < playArea[i].length; j++)
				if(playArea[i][j] == fragment.getValue())
					playArea[i][j] = 0;
		// Put a new fragment.
		PutFragment(fragment);
	}
}