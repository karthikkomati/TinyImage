package uk.ac.soton.ecs.kk8g18;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.vfs2.FileSystemException;
import org.openimaj.data.dataset.GroupedDataset;
import org.openimaj.data.dataset.ListDataset;
import org.openimaj.data.dataset.VFSGroupDataset;
import org.openimaj.data.dataset.VFSListDataset;
import org.openimaj.experiment.dataset.split.GroupedRandomSplitter;
import org.openimaj.feature.DoubleFV;
import org.openimaj.image.FImage;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.processing.resize.ResizeProcessor;
import org.openimaj.knn.DoubleNearestNeighboursExact;
import org.openimaj.util.array.ArrayUtils;
import org.openimaj.util.pair.IntDoublePair;

public class Part1 {
	
	static int k = 25;
	
	public static void test() throws FileSystemException {
		
		GroupedDataset<String, VFSListDataset<FImage>, FImage> allData = new VFSGroupDataset<FImage>("C:\\Users\\karth\\Desktop\\training\\training",ImageUtilities.FIMAGE_READER);
		GroupedRandomSplitter<String, FImage> t =  new GroupedRandomSplitter<String, FImage>(allData, 50, 0, 50);
		GroupedDataset<String, ListDataset<FImage>, FImage> trainingSet = t.getTrainingDataset();
		System.out.println(trainingSet.getGroups());
		
		HashMap<double[], String> featureVectors = new HashMap<double[],String>();
		ArrayList<double[]> vectors = new ArrayList<double[]>();
		ArrayList<DoubleFV> v = new ArrayList<DoubleFV>();
		ArrayList<String> classes = new ArrayList<String>();
		
		for(Entry<String, ListDataset<FImage>> group : trainingSet.entrySet()) {
			
			for(FImage trainingImage: group.getValue()) {
				vectors.add(normalise(extractFeature(trainingImage)));
				v.add(new DoubleFV(normalise(extractFeature(trainingImage))));
				classes.add(group.getKey());
				featureVectors.put(normalise(extractFeature(trainingImage)), group.getKey());
				
			}
			
			
			
		}
		
		//double[][] a = vectors.toArray(new double[][] {});
		
		DoubleNearestNeighboursExact knn = new DoubleNearestNeighboursExact(vectors.toArray(new double[][] {}));
		DoubleNearestNeighboursExact knn2 = new DoubleNearestNeighboursExact(featureVectors.keySet().toArray(new double[][] {}));
		
		
		
		//testing!!!
//		Map<FImage,String> r = new HashMap<FImage,String>();
//		double to = 0;
//		double co = 0;
//		
//		for(Entry<String, ListDataset<FImage>> images : t.getTestDataset().entrySet()) {
//			
//			
//			for(FImage testImage: images.getValue()) {
//				
//				List<IntDoublePair> neighbours = knn.searchKNN(normalise(extractFeature(testImage)),k);
//				Map<String,Integer> classCount = new HashMap<String,Integer>();
//				for(IntDoublePair result: neighbours){
//					//Get neighbour class
//					String resultClass = classes.get(result.first);
//					
//					int newCount = 1;
//					//Retrieve existing count for the class
//					if(classCount.containsKey(resultClass)){
//						newCount += classCount.get(resultClass);
//					}
//					
//					//Add 1 to class count
//					classCount.put(resultClass, newCount);		
//				        
//					
//					
//				}
//				
//				List<Map.Entry<String, Integer>> guessList = new ArrayList<Map.Entry<String, Integer>>(classCount.entrySet());
//				
//				//Sort list with greatest count first
//				Collections.sort(guessList, new Comparator<Map.Entry<String, Integer>>(){
//					public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
//						return o2.getValue().compareTo(o1.getValue());
//					}
//				});
//				
//				r.put(testImage, guessList.get(0).getKey());
//				if(guessList.get(0).getKey().equals(images.getKey())) {
//					co++;
//				}
//				to++;
//				//System.out.println(guessList.get(0).getKey());
//				
//			}
//			
//			
//			
//		}
//		System.out.println(co);
//		System.out.println(to);
//		double correct = (co/to)*100;
//		System.out.println(correct+"%");
		
	
		
		VFSListDataset<FImage> testing = new VFSListDataset<FImage>("C:\\Users\\karth\\Desktop\\testing",ImageUtilities.FIMAGE_READER);
		Map<String,String> ma = new HashMap<String,String>();
		for(int j=0;j<testing.size();j++) {
			
			List<IntDoublePair> neighbours = knn.searchKNN(normalise(extractFeature(testing.get(j))), k);
			
			Map<String,Integer> classCount = new HashMap<String,Integer>();
			
			
			
			//For all neighbours
			for(IntDoublePair result: neighbours){
				//Get neighbour class
				String resultClass = classes.get(result.first);
				
				int newCount = 1;
				//Retrieve existing count for the class
				if(classCount.containsKey(resultClass)){
					newCount += classCount.get(resultClass);
				}
				
				//Add 1 to class count
				classCount.put(resultClass, newCount);		
			        
				
				
			}
			
			List<Map.Entry<String, Integer>> guessList = new ArrayList<Map.Entry<String, Integer>>(classCount.entrySet());
			
			//Sort list with greatest count first
			Collections.sort(guessList, new Comparator<Map.Entry<String, Integer>>(){
				public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2){
					return o2.getValue().compareTo(o1.getValue());
				}
			});
			
			//Confidence in result
			//double confidence = guessList.get(0).getValue().doubleValue() / (double) k;
			
			//Guessed class is first in list
			String guessedClass = guessList.get(0).getKey();
			
			ma.put(testing.getID(j), guessedClass);
			
		}
		
		 File file = new File("C:\\Users\\karth\\Desktop\\t1.txt");
	        
	        BufferedWriter bf = null;;
	        
	        try{
	            
	            //create new BufferedWriter for the output file
	            bf = new BufferedWriter( new FileWriter(file) );
	 
	            //iterate map entries
	            for(Map.Entry<String, String> entry : ma.entrySet()){
	                
	                //put key and value separated by a colon
	                bf.write( entry.getKey() + " " + entry.getValue() );
	                
	                //new line
	                bf.newLine();
	            }
	            
	            bf.flush();
	 
	        }catch(IOException e){
	            e.printStackTrace();
	        }finally{
	            
	            try{
	                //always close the writer
	                bf.close();
	            }catch(Exception e){}
	        }
	
	}
	
	
	public static double[] normalise(double[] vector) {
		double sum = 0;
		for(double d: vector) {
			sum += d;
		
		}
		for(int i = 0; i<vector.length;i++) {
			vector[i] /= sum;
		}
		
		return vector;
	}
	
	public static double[] extractFeature(FImage image) {
		
		//Smallest dimension of image is the biggest the square can be
		int size = Math.min(image.width, image.height);
		
		//Extract the square from centre
		FImage center = image.extractCenter(size, size);

		//Resize image to tiny image
		FImage small = center.process(new ResizeProcessor(32,32));
		
		//DisplayUtilities.display(small);
		
		//2D array to 1D vector
		return ArrayUtils.reshape(ArrayUtils.convertToDouble(small.pixels));
	}

}
