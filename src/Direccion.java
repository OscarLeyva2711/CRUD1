public class Direccion {
    private int id;
    private int personaId;
    private String descripcion;

    public Direccion(int id, int personaId, String descripcion) {
        this.id = id;
        this.personaId = personaId;
        this.descripcion = descripcion;
    }

    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getDescripcion() { return descripcion; }

    public void setId(int id) { this.id = id; }
    public void setPersonaId(int personaId) { this.personaId = personaId; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }


}