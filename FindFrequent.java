/*
@author: Satnam Singh

This implementation to find the frequent pairs is independent
of the type of input.

 */

package Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

public class FindFrequent {
    static HashMap<String, String> candidateList = new HashMap<String, String>();
    static int threshold = 0;
    public static void main(String args[]) throws IOException
    {
        long startTime = System.nanoTime();
        //System.out.println("Importing file...");
        String filePath = "/home/user/Downloads/Input/Sample_3.txt";
        BufferedReader fileIo = new BufferedReader(new FileReader(filePath));
        String line = fileIo.readLine();
        threshold = Integer.parseInt(line);
        //threshold = 18;
        String itemList[] = getItemList(fileIo);
        fileIo.close();
        HashMap<String, String> bucket_items = getBucketList(itemList);
        ArrayList<HashMap> hashList = getItemCount(bucket_items);
        HashMap<String, Integer> item_count= hashList.get(0);
        HashMap<String, Set<String>> itemBucketMap = hashList.get(1);

        ConcurrentSkipListMap<String, Integer> commonBuckets = getCommonBuckets(itemBucketMap);
        ConcurrentSkipListMap<String, Integer> prunedCommonBuckets = pruneThis(commonBuckets);
        //System.out.println("Time prune: " + (System.nanoTime() - pruneThisStart));
        PrintWriter writer = new PrintWriter("/home/user/Downloads/Input/result.txt", "UTF-8");
        writer.println(prunedCommonBuckets.keySet());
        writer.close();
        System.out.println("TIME TOTAL: " + (System.nanoTime() - startTime));
    }

    public static String[] getItemList(BufferedReader itemstring)throws IOException{
        String itemList[] = itemstring.readLine().split("\t");
        return itemList;
    }

    public static HashMap<String, String> getBucketList(String itemList[])
    {
        HashMap<String, String> bucket_items = new HashMap<String, String>();
        for(String set : itemList)
        {
            bucket_items.put(set.split(",")[0], set.substring((set.indexOf(",") + 1), set.length()));
        }
        return bucket_items;
    }

    public static ArrayList<HashMap> getItemCount(HashMap<String, String> bucket_items)
    {
        HashMap<String, Integer> item_count = new HashMap<String, Integer>();
        HashMap<String, Set<String>> itemBucketMap = new HashMap<String, Set<String>>();
        ArrayList<HashMap> hashList = new ArrayList<>();
        for(String bucket : bucket_items.keySet())
        {
            String itemString = bucket_items.get(bucket);
            String itemArray[] = itemString.split(",");
            for(String item : itemArray)
            {
                if(itemBucketMap.containsKey(item))
                {
                    Set<String> value = itemBucketMap.get(item);
                    value.add(bucket);
                    itemBucketMap.put(item, value);
                }
                else
                {
                    HashSet buckets = new HashSet<String>();
                    //Set<String> buckets = new HashSet<String>();
                    buckets.add(bucket);
                    itemBucketMap.put(item, buckets);
                }
//                if(item_count.containsKey(item))
//                {
//                    item_count.put(item, (item_count.get(item) + 1));
//                }
//                else
//                {
//                    item_count.put(item, 1);
//                }
            }
        }
        hashList.add(item_count);
        hashList.add(itemBucketMap);
        return hashList;
    }

    public static ConcurrentSkipListMap<String, Integer> getCommonBuckets(HashMap<String, Set<String>> itemBucketMap)
    {
        String newDivide = ",";
        ConcurrentSkipListMap<String, Integer> pairCount = new ConcurrentSkipListMap<>();//only store length later
        for(String item1 : itemBucketMap.keySet())
        {
            Set<String> bucketSet1 = itemBucketMap.get(item1);
            for(String item2 : itemBucketMap.keySet())
            {
                Set<String> commonBuckets = new HashSet<String>(bucketSet1);
                if (item2.compareToIgnoreCase(item1)<=0)
                    continue;
                else
                {
                    Set<String> bucketSet2 = itemBucketMap.get(item2);
                    commonBuckets.retainAll(bucketSet2);
                }
                String keyPairCount = item1 + newDivide + item2;
                pairCount.put(keyPairCount, commonBuckets.size());
            }

        }
        return pairCount;
    }

    public static ConcurrentSkipListMap<String, Integer> pruneThis(ConcurrentSkipListMap<String, Integer> commonBuckets)
    {
        for(String pair : commonBuckets.keySet())
        {
            if(commonBuckets.get(pair) < threshold)
            {
                commonBuckets.remove(pair);
            }
        }
        return commonBuckets;
    }

}