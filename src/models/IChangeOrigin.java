package models;

public interface IChangeOrigin {
    public void SetOrigin(models.Point p);
    public Point GetOrigin();
    void SetSize(int i);
    int GetSize();
    public void calculateInsidePoints();

}
