
/*** Author :Vibhav Gogate
The University of Texas at Dallas
*****/


import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
 
public class Kmeans {
    public static void main(String [] args){
	/* if (args.length < 3){
	    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
	    return;
	} */
	try{
	//	URL url = new URL(getCodeBase(), "examples/strawberry.jpg");
	    BufferedImage originalImage = ImageIO.read(new File(args[0]));
	    int k=Integer.parseInt(args[1]);
	    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
	    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
	    
	}catch(IOException e){
	    System.out.println(e.getMessage());
	}	
    }
    
    private static BufferedImage kmeans_helper(BufferedImage originalImage, int k){
	int w=originalImage.getWidth();
	int h=originalImage.getHeight();
	BufferedImage kmeansImage = new BufferedImage(w,h,originalImage.getType());
	Graphics2D g = kmeansImage.createGraphics();
	g.drawImage(originalImage, 0, 0, w,h , null);
	// Read rgb values from the image
	int[] rgb=new int[w*h];
	int red[]=new int[w*h];
	int green[]=new int[w*h];
	int blue[]=new int[w*h];
	int count=0;
	int min=0;
	int max=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		rgb[count++]=kmeansImage.getRGB(i,j);
		if(count==1){
			min=kmeansImage.getRGB(i,j);
			max=kmeansImage.getRGB(i,j);
		}
		else
		{
			if(min>kmeansImage.getRGB(i,j))
			{
				min=kmeansImage.getRGB(i,j);
			}
			if(max<kmeansImage.getRGB(i,j))
			{
				max=kmeansImage.getRGB(i,j);
			}
		}
		
		
	    }
	}
	// Call kmeans algorithm: update the rgb values
	kmeans(rgb,k,min,max);

	// Write the new rgb values to the image
	count=0;
	for(int i=0;i<w;i++){
	    for(int j=0;j<h;j++){
		kmeansImage.setRGB(i,j,rgb[count++]);
	    }
	}
	return kmeansImage;
    }

    // Your k-means code goes here
    // Update the array rgb by assigning each entry in the rgb array to its cluster center
    private static void kmeans(int[] rgb, int k,int min,int max){
    	
    	int red[]=new int[rgb.length];
    	int green[]=new int[rgb.length];
    	int blue[]=new int[rgb.length];
    	int karray[][]=new int[k][3];
       boolean intilize_flag=true;
       int rand_rgb;
       Random randomGenerator = new Random();
      
       for(int i=0;i<k;i++)
       {
    	   rand_rgb=randomGenerator.nextInt((max - min) + 1) + min;
    	  
    	   karray[i][0]=(rand_rgb >> 16) & 0x000000FF;
    	   karray[i][1]=(rand_rgb >>8 ) & 0x000000FF;;
    	   karray[i][2]=(rand_rgb) & 0x000000FF;;
    	   //System.out.println(karray[i][0]+" "+karray[i][1]+" "+karray[i][2]);
		   
		   System.out.println("Cluster "+i+" R value "+karray[i][0]+" G value "+karray[i][1]+" B value "+karray[i][2]);
       }
       ArrayList<Integer>[] k_values =(ArrayList<Integer>[])new ArrayList[k];
       for(int i=0;i<k;i++)
       {
       k_values[i]=new ArrayList<Integer>();
       }
       for(int i=0;i<=25;i++)
       {
           if(i!=0)
    	   for(int z=0;z<k;z++)
           {
           k_values[z].clear();
           }
       	   
       formk_array(red,green,blue,rgb,karray,k_values,k,min,max);
       //System.out.println(k_values[0].size()+" "+k_values[1].size()+" "+k_values[2].size());
       re_intilize_array(red,green,blue,rgb,karray,k_values,k);
       }
       allocate_one_color(red,green,blue,rgb,karray,k_values,k,min,max);
       }
      public static void formk_array(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> k_values[],int k,int min,int max)
      {
    	
    	  for(int j=0;j<rgb.length;j++)
      	{
              double minimum=0;
              int index=0;
              
      		red[j] = (rgb[j] >> 16) & 0x000000FF;
      		green[j] = (rgb[j] >>8 ) & 0x000000FF;
      		blue[j] = (rgb[j]) & 0x000000FF;
      		for(int z=0;z<k;z++){
      		double one=Math.pow((karray[z][0]-red[j]),2)+Math.pow((karray[z][1]-green[j]),2)+Math.pow((karray[z][2]-blue[j]),2);
            if(z==0)
            {
            	minimum=one;
      			index=z;
            }
            else
            {
            	if(one<=minimum)
          		{
          			minimum=one;
          			index=z;
          		}
            }

      		}
      		
      		k_values[index].add(j); 
      	}
      }
      public static void re_intilize_array(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> k_values[],int k)
      {
    	int index;
      	int red_value=0;
      	int green_value=0;
      	int blue_value=0;
      	int divide;
      	for(int z=0;z<k;z++){
      	for(int i=0;i<k_values[z].size();i++)
      	{
      	         index=k_values[z].get(i);
      	         red_value=red_value+red[index];
      	         green_value=green_value+green[index];
      	         blue_value=blue_value+blue[index];
      	}
      	if(k_values[z].size()==0)
      	{
            divide=1;
      	}
      	else
      	{
      		divide=k_values[z].size();
      	}
      	
      	karray[z][0]=red_value/divide;
      	karray[z][1]=green_value/divide;
      	karray[z][2]=blue_value/divide;
      	red_value=0;
      	green_value=0;
      	blue_value=0;
      	
      	}
      	
      }
      public static void allocate_one_color(int red[],int green[],int blue[],int rgb[],int karray[][],ArrayList<Integer> k_values[],int k,int min,int max)
      {
    	int value;
    	int index;
    	int sum=0;
    	
    	for(int z=0;z<k;z++){
    		HashMap<Integer, Integer> hm = new HashMap();
    	if(k_values[z].size()==0)
      	{
            value=0;
      	}
      	else
      	{   sum=0;
      	for(int i=0;i<k_values[z].size();i++)
      	{
      		index=k_values[z].get(i);
      		try{
      		hm.put(rgb[index], hm.get(rgb[index])+1);
      		}
      		catch(Exception e)
      		{
      			hm.put(rgb[index], 1);
      		}
 	        sum=rgb[index]+sum;
 	        //if(z==0 && i>1000 && i<3000)
 	        {
 	        	//System.out.print(index+" "+rgb[index]+" ");
 	        }
      	}
      	int value_max=0;
      	int index_max=0;
      	Iterator it = hm.entrySet().iterator();
      	Map.Entry pair = (Map.Entry)it.next();
      	value_max=(int)pair.getValue();
      	index_max=(int)pair.getKey();
      	while(it.hasNext())
      	{
      		Map.Entry pair1 = (Map.Entry)it.next();
      		if((int)pair1.getValue()>value_max){
      			
      			value_max=(int)pair1.getValue();
      			index_max=(int)pair1.getKey();
      			
      		}
      		
      	}
      	for(int y=min;y<=max;y++)
      	{
      		
      	}
      		//value=sum/k_values[z].size();
      	value=index_max;
      		//System.out.println("changing values to........."+value);
      	}
    	for(int i=0;i<k_values[z].size();i++)
      	{
      	         index=k_values[z].get(i);
      	         rgb[index]=value;
      	}
    	}
    	
      }
    }

