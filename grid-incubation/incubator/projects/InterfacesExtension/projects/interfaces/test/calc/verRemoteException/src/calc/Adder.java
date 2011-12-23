package calc;

import java.rmi.RemoteException;

import java.io.FileNotFoundException;

public interface Adder {
  public int add(int x, int y) throws FileNotFoundException, RemoteException;
  public int add3(int x, int y, int z) throws RemoteException;
}