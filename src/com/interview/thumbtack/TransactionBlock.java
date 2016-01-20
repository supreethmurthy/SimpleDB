package com.interview.thumbtack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by supreethmurthy on 1/15/16.
 */
public class TransactionBlock
{
    private Map<String,Integer> nameValueMap = new HashMap<>();
    private Map<Integer,Integer> valueCountMap = new HashMap<>();

    //Copy constructor
    public TransactionBlock(TransactionBlock transactionBlock)
    {
        Map<String,Integer> nameValueMap =  transactionBlock.nameValueMap;
        Map<Integer,Integer> valueCountMap = transactionBlock.valueCountMap;

        //Iterate over the maps and do a deep copy
        for (String key: nameValueMap.keySet())
        {
            Integer value = nameValueMap.get(key);
            this.nameValueMap.put(key,value);
        }
        for (Integer key: valueCountMap.keySet())
        {
            Integer value = valueCountMap.get(key);
            this.valueCountMap.put(key,value);
        }
    }
    //Default constructor
    public TransactionBlock()
    {

    }

    public Integer getValue(String key)
    {
        return nameValueMap.get(key);
    }

    public void setValue(String key, Integer value)
    {
        //If that key has an older value, decrement
        //the count in the valueCountMap
        if (nameValueMap.get(key)!=null)
        {
            Integer existingValue = nameValueMap.get(key);
            manageValueCountMap(existingValue,false);
        }
        nameValueMap.put(key,value);
        manageValueCountMap(value, true);
    }

    public void unsetValue(String key)
    {
        if (nameValueMap.get(key) !=null)
        {
            Integer value = nameValueMap.get(key);
            nameValueMap.remove(key);
            manageValueCountMap(value, false);
        }
        else
        {
            System.out.println("NOTHING TO UNSET");
        }
    }

    public Integer getNumEqualTo(Integer value)
    {
        if (valueCountMap.get(value)!=null)
        {
            return valueCountMap.get(value);
        }
        return 0;
    }

    private void manageValueCountMap (Integer value, Boolean increment)
    {
        Integer valueCount = 1;
        if (valueCountMap.get(value) != null)
        {
            valueCount = valueCountMap.get(value);
            valueCount = valueCount - 1;
            if (increment)
            {
                valueCount = valueCount + 2;
            }
        }
        valueCountMap.put(value, valueCount);
        //This is to clear all the old values which were previously referenced
        if (valueCount == 0)
        {
            valueCountMap.remove(value);
        }
    }
}
