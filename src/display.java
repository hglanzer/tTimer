import java.awt.Color;

public class display {
	public static final int POS = 6;
	public static final int SEGMENTS = 7;
	private Color[][] digits = new Color[POS][SEGMENTS];
	

	public Color ON = Color.green;
    public Color OFF = Color.green.darker().darker().darker().darker().darker().darker().darker(); 
    public Color RED = Color.red;
    public Color WHITE = Color.white;

    public Color[][] actualColor = new Color[10][7];
       
    private Color digitsColorWHITE[][] =
        {
          	{ WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, OFF },
          	{ OFF, WHITE, WHITE, OFF, OFF, OFF, OFF },
       		{ WHITE, WHITE, OFF, WHITE, WHITE, OFF, WHITE },
       		{ WHITE, WHITE, WHITE, WHITE, OFF, OFF, WHITE },
       		{ OFF, WHITE, WHITE, OFF, OFF, WHITE, WHITE },
       		{ WHITE, OFF, WHITE, WHITE, OFF, WHITE, WHITE },	
       		{ WHITE, OFF, WHITE, WHITE, WHITE, WHITE, WHITE },
       		{ WHITE, WHITE, WHITE, OFF, OFF, OFF, OFF },
           	{ WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE },
           	{ WHITE, WHITE, WHITE, WHITE, OFF, WHITE, WHITE } 		
        };
    
    private Color digitsColorRED[][] =
    {
      	{ RED, RED, RED, RED, RED, RED, OFF },
      	{ OFF, RED, RED, OFF, OFF, OFF, OFF },
   		{ RED, RED, OFF, RED, RED, OFF, RED },
   		{ RED, RED, RED, RED, OFF, OFF, RED },
   		{ OFF, RED, RED, OFF, OFF, RED, RED },
   		{ RED, OFF, RED, RED, OFF, RED, RED },		
   		{ RED, OFF, RED, RED, RED, RED, RED },
   		{ RED, RED, RED, OFF, OFF, OFF, OFF },
       	{ RED, RED, RED, RED, RED, RED, RED },
       	{ RED, RED, RED, RED, OFF, RED, RED } 		
    };
    
    private Color digitsColorNORMAL[][] =
    {
    	{ ON, ON, ON, ON, ON, ON, OFF },   	
    	{ OFF, ON, ON, OFF, OFF, OFF, OFF },
    	{ ON, ON, OFF, ON, ON, OFF, ON },	
    	{ ON, ON, ON, ON, OFF, OFF, ON },
    	{ OFF, ON, ON, OFF, OFF, ON, ON },  	
    	{ ON, OFF, ON, ON, OFF, ON, ON },    	
   		{ ON, OFF, ON, ON, ON, ON, ON },
    	{ ON, ON, ON, OFF, OFF, OFF, OFF },
   		{ ON, ON, ON, ON, ON, ON, ON },
   		{ ON, ON, ON, ON, OFF, ON, ON } 		
    };
    
    // schema: A - B - C - D - E - F - G = index
	private Color zero[] = { ON, ON, ON, ON, ON, ON, OFF };
    private Color one[] = { OFF, ON, ON, OFF, OFF, OFF, OFF };
    private Color two[] = { ON, ON, OFF, ON, ON, OFF, ON };
    private Color three[] = { ON, ON, ON, ON, OFF, OFF, ON };
    private Color four[] = { OFF, ON, ON, OFF, OFF, ON, ON };
    private Color five[] = { ON, OFF, ON, ON, OFF, ON, ON };
    
    private Color six[] = { ON, OFF, ON, ON, ON, ON, ON };
    private Color seven[] = { ON, ON, ON, OFF, OFF, OFF, OFF };
    private Color eight[] = { ON, ON, ON, ON, ON, ON, ON };
    private Color nine[] = { ON, ON, ON, ON, OFF, ON, ON };
    
    public display(int[] tmp)
    {	
    	actualColor = digitsColorNORMAL;
   		this.setTime(tmp);
    }
    
    public void setRED()
    {
    	actualColor = digitsColorRED;    	
    }
    public void setNORMAL()
    {
    	actualColor = digitsColorNORMAL;
    }
    public void setWHITE()
    {
    	actualColor = digitsColorWHITE;
    }
    public void setTime(int time[])
    {
    	//System.out.printf("tick: %d%d:%d%d:%d%d\n", time[5], time[4], time[3], time[2], time[1], time[00]);
    	for(int i = 0; i < POS; i++)
    	{
    		Color[] number = new Color[SEGMENTS];
    		switch (time[i])
            {
                case 0:
                   // number = zero;
                	number = actualColor[0];
                    break;
                case 1:
                    number = one;
                	number = actualColor[1];
                    break;
                case 2:
                    number = two;
                	number = actualColor[2];
                    break;
                case 3:
                    number = three;
                	number = actualColor[3];
                    break;
                case 4:
                    number = four;
                	number = actualColor[4];
                    break;
                case 5:
                    number = five;
                	number = actualColor[5];
                    break;
                case 6:
                    number = six;
                	number = actualColor[6];
                    break;
                case 7:
                    number = seven;
                	number = actualColor[7];
                    break;
                case 8:
                    number = eight;
                	number = actualColor[8];
                    break;
                case 9:
                    number = nine;
                	number = actualColor[9];
                    break;
                default: /* other number */
                    number = zero;
                    break;
            }
    		digits[(POS-i)-1] = number;
    	}
    }
    
    public Color getColorVals(int pos, int seg)
    {
    	return digits[pos][seg];
    }
       
    public void printValueStdOut()
    { 	
    	for(int i = 0; i < POS; i++)
    	{
    		for(int seg = 0; seg < SEGMENTS; seg++)
    			System.out.print(digits[i][seg]);
    		System.out.printf("\n");
    	}	
		System.out.printf("-------------------------\n");    	
    }
}
