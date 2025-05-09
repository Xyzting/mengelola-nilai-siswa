package com.example;

public class Student implements Comparable<Student> {
  private String name;
  private String nim;
  private double nilai1, nilai2, nilai3;

  public Student(String name, String nim, double nilai1, double nilai2, double nilai3) {
      this.name = name;
      this.nim = nim;
      this.nilai1 = nilai1;
      this.nilai2 = nilai2;
      this.nilai3 = nilai3;
  }

  public String getName() { return name; }
  public String getNim() { return nim; }
  public double getNilai1() { return nilai1; }
  public double getNilai2() { return nilai2; }
  public double getNilai3() { return nilai3; }
  public double getAverage() { return (nilai1 + nilai2 + nilai3) / 3; }

  @Override
  public int compareTo(Student other) {
      return Double.compare(other.getAverage(), this.getAverage());
  }

  @Override
  public String toString() {
      return nim + "," + name + "," + nilai1 + "," + nilai2 + "," + nilai3;
  }
}
