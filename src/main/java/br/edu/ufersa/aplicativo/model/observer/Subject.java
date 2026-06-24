package br.edu.ufersa.aplicativo.model.observer;

public interface Subject {
    void attach(Observer observer);
    void detach(Observer observer);
    void notifyObservers();
}
