package com.interview.thumbtack;

import java.io.*;
import java.util.*;

/**
 * Created by supreethmurthy on 1/15/16.
 */
public class Database
{
    //Have a stack of transaction blocks for all the transaction
    private Stack<TransactionBlock> transactionBlocks = new Stack<>();
    //Maintain reference to the current block
    private TransactionBlock currentBlock = null;
    private BufferedReader bufferedReader = null;

    public Database()
    {
        TransactionBlock transactionBlock = new TransactionBlock();
        transactionBlocks.push(transactionBlock);
        currentBlock = transactionBlock;
    }

    public void beginTransaction()
    {
        //Create a new transaction for every begin and push it to the stack
        //and update the current transaction block
        TransactionBlock transactionBlock = new TransactionBlock(currentBlock);
        transactionBlocks.push(transactionBlock);
        currentBlock = transactionBlock;
    }

    public void rollback()
    {
        //There will be 1 transaction at all times in the stack
        //Only if there is more than 1 is a rollback allowed
        if (transactionBlocks.size()>1)
        {
            transactionBlocks.pop();
            currentBlock = transactionBlocks.peek();
        }
        else
        {
            System.out.println("NO TRANSACTION");
        }
    }

    public void commit()
    {
        //If there are multiple transaction blocks
        //Get the most recent and clear all the blocks
        //Push the most recent and update the current block
        if (transactionBlocks.size()>1)
        {
            TransactionBlock latestBlock = transactionBlocks.pop();
            transactionBlocks.clear();
            transactionBlocks.push(latestBlock);
            currentBlock = latestBlock;
        }
        else
        {
            System.out.println("NO TRANSACTION");
        }
    }

    public Integer getValue(String key)
    {
        return currentBlock.getValue(key);
    }

    public void setValue(String key, Integer value)
    {
        currentBlock.setValue(key, value);
    }

    public void unsetValue(String key)
    {
        currentBlock.unsetValue(key);
    }

    public Integer getNumEqualTo(Integer value)
    {
        return  currentBlock.getNumEqualTo(value);
    }

    public void processInputCommand(String cmd)
    {
        try
        {
            String[] cmds = cmd.split(" ");
            //Input commands can be either 1 word (BEGIN, COMMIT, ROLLBACK, EXIT)
            //2 Words (GET, NUMEQUALTO, UNSET)
            //3 Words (SET)
            String command = "";
            String variable = "";
            Integer value = 0;
            command = cmds[0].trim();
            if (cmds.length == 3)
            {
                variable = cmds[1].trim();
                value = Integer.parseInt(cmds[2].trim());
            }
            if (cmds.length == 2)
            {
                if (command.equalsIgnoreCase("GET") ||
                        command.equalsIgnoreCase("UNSET"))
                {
                    variable = cmds[1].trim();
                }
                else if(command.equalsIgnoreCase("NUMEQUALTO"))
                {
                    value = Integer.parseInt(cmds[1].trim());
                }
            }

            switch(command.toUpperCase())
            {
                case "SET":
                {
                    setValue(variable,value);
                    break;
                }
                case "GET":
                {
                    Integer output = getValue(variable);
                    if (output!=null)
                    {
                        System.out.println(output);
                    }
                    //This is just to display NULL in uppercase
                    else
                    {
                        System.out.println("NULL");
                    }

                    break;
                }
                case "NUMEQUALTO":
                {
                    Integer output = getNumEqualTo(value);
                    System.out.println(output);
                    break;
                }
                case "UNSET":
                {
                    unsetValue(variable);
                    break;
                }
                case "BEGIN":
                {
                    beginTransaction();
                    break;
                }
                case "COMMIT":
                {
                    commit();
                    break;
                }
                case "ROLLBACK":
                {
                    rollback();
                    break;
                }
                case "END":
                {
                    System.exit(0);
                }
                default:
                {
                    System.out.println("Not able to process input. Please try again");
                    System.exit(-1);
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println("Not able to process input. Please try again");
            System.exit(-1);
        }

    }

    private void testAll()
    {
        //Different sets of inputs given in the example
        String[] commands1 = {
                "SET ex 10",
                "GET ex",
                "UNSET ex",
                "GET ex"
        };
        String[] commands2 = {
                "SET a 10",
                "SET b 10",
                "NUMEQUALTO 10",
                "NUMEQUALTO 20",
                "SET b 30",
                "NUMEQUALTO 10"
        };
        String[] commands3 = {
                "BEGIN",
                "SET a 10",
                "GET a",
                "BEGIN",
                "SET a 20",
                "GET a",
                "ROLLBACK",
                "GET a",
                "ROLLBACK",
                "GET a"
        };
        String[] commands4 = {
                "BEGIN",
                "SET a 30",
                "BEGIN",
                "SET a 40",
                "COMMIT",
                "GET a",
                "ROLLBACK"
        };
        String[] commands5 = {
                "SET a 50",
                "BEGIN",
                "GET a",
                "SET a 60",
                "BEGIN",
                "UNSET a",
                "GET a",
                "ROLLBACK",
                "GET a",
                "COMMIT",
                "GET a"
        };
        String[] commands6 = {
                "SET a 10",
                "BEGIN",
                "NUMEQUALTO 10",
                "BEGIN",
                "UNSET a",
                "NUMEQUALTO 10",
                "ROLLBACK",
                "NUMEQUALTO 10",
                "COMMIT"
        };

        for (String cmd: commands6)
        {
            processInputCommand(cmd);
        }
    }

    private String readLine()
    {
        String line = "";
        try
        {
            bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            line = bufferedReader.readLine().trim();
            return line;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return line;
        }

    }

    private void processInputFile (String filepath)
    {
        List<String> cmdList = new ArrayList<String>();
        Scanner scanner = null;
        try
        {
            scanner = new Scanner(new File(filepath));
            String text = scanner.useDelimiter("//A").next();
            String[] commands = text.split("\n");

            for (String cmd: commands)
            {
                processInputCommand(cmd);
            }
        }
        catch (Exception e)
        {
            System.out.println("Error occurred while reading file. Please check the file path is correct and/or the file contents are correct.");
            System.exit(-1);
        }

        finally
        {
            try
            {
                if (scanner!=null)
                {
                    scanner.close();
                }
            }
            catch (Exception ex)
            {
                //Eat the exception
                System.exit(-1);
            }
        }

    }

    public static void main(String[] args)
    {
        /*Database db = new Database();
        db.testAll();*/
        Database db = new Database();
        //File path Ex= /Users/supreethmurthy/Documents/inputcmd.txt
        System.out.println("Enter a DB command. To process a file, enter File and the file path with file name.");
        try
        {
            String cmd = "";
            while(!cmd.trim().equalsIgnoreCase("END"))
            {
                cmd = db.readLine();
                if (!cmd.toUpperCase().contains("FILE"))
                {
                    db.processInputCommand(cmd);
                }
                else
                {
                    String[] fileCmds = cmd.split(" ");
                    String filepath = fileCmds[1].trim();
                    db.processInputFile(filepath);
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                db.bufferedReader.close();
            }
            catch (Exception ex)
            {
                System.exit(-1);
            }
        }
    }
}
