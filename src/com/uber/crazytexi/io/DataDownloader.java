package com.uber.crazytexi.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataDownloader {

  private static final String DATA_PATH = "data";
  private static final Pattern tripDataPattern = Pattern.compile(
      "(https://nyctaxitrips.blob.core.windows.net/data/(trip_data_\\d+\\.csv\\.zip))");
  private static final Pattern fareDataPattern = Pattern.compile(
      "(https://nyctaxitrips.blob.core.windows.net/data/(trip_fare_\\d+\\.csv\\.zip))");

  private static void download(File file, String urlString) throws IOException {
    if (file.exists()) {
      file.delete();
    }
    file.createNewFile();
    BufferedReader input = null;
    Writer writer = null;
    try {
      writer = new OutputStreamWriter(new FileOutputStream(file));
      URL url = new URL(urlString);
      URLConnection connection = url.openConnection();
      input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      char[] b = new char[1024];
      int count;
      while ((count = input.read(b)) >= 0) {
        writer.write(b, 0, count);
      }
      writer.flush();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      input.close();
      writer.close();
    }
  }

  public static void main(String[] args) throws MalformedURLException, IOException {
    Scanner scanner = null;
    
    try {
      URLConnection connection = new URL("http://www.andresmh.com/nyctaxitrips/").openConnection();
      scanner = new Scanner(connection.getInputStream());
      scanner.useDelimiter("\\Z");
      String content = scanner.next();
      
      File data = new File(DATA_PATH);
      if (!data.exists()) {
        data.mkdir();
      }
      Matcher matcher = tripDataPattern.matcher(content);
      while (matcher.find()) {
        System.out.println("wget -O " + matcher.group(2) + " '" + matcher.group(1) + "'");
        //download(new File(data, matcher.group(2) + ".zip"), matcher.group(1));
      }
      matcher = fareDataPattern.matcher(content);
      while (matcher.find()) {
        System.out.println("wget -O " + matcher.group(2) + " '" + matcher.group(1) + "'");
        //download(new File(data, matcher.group(2) + ".zip"), matcher.group(1));
      }
    } finally {
      scanner.close();
    }
  }
}
