public class Telefono {
    private int id;
    private int personaId;
    private String numero;

    public Telefono(int id, int personaId, String numero) {
        this.id = id;
        this.personaId = personaId;
        this.numero = numero;
    }

    public int getId() { return id; }
    public int getPersonaId() { return personaId; }
    public String getNumero() { return numero; }

    public void setId(int id) { this.id = id; }
    public void setPersonaId(int personaId) { this.personaId = personaId; }
    public void setNumero(String numero) { this.numero = numero; }


}
