package tda;


import com.google.gson.annotations.SerializedName;

public class Pokemon {

    @SerializedName("nombre")
    private String nombre;

    @SerializedName("tipo")
    private String tipo;

    @SerializedName("nivel")
    private int nivel;

    public Pokemon() {}  // necesario para Gson

    public Pokemon(String nombre, String tipo, int nivel) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivel = nivel;
    }

    public String getNombre() { return nombre; }
    public String getTipo() { return tipo; }
    public int getNivel() { return nivel; }

    @Override
    public String toString() {
        return "Pokemon{" +
                "nombre='" + nombre + '\'' +
                ", tipo='" + tipo + '\'' +
                ", nivel=" + nivel +
                '}';
    }
}
