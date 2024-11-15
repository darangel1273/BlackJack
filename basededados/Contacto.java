package basededados;
import java.sql.Blob;
import java.sql.Date;
/**
 * SubClasse Contacto que tem como SuperClasse Pessoa.
 * @see basededados.Pessoa;
 * 
 * @author (Ruin Mantel Die Oliver Prier) 
 * @version (1.0)
 */
public class Contacto extends Pessoa{
    private double Tlm;
    private String Email;
    /**
     * Construtor para objetos da classe Contacto
     */
    public Contacto(String apelido,Date dataNasc,Blob foto,String morada,String nome,double numero,double telemovel,String email){
        super(apelido, dataNasc, foto, morada, nome, numero);//Chamada ao construtor da superclasse: Pessoa
        setTelemovel(telemovel);                            // Encapsular o numero de Telemovel.
        setEmail(email);                                    // Encapsular o Email do Contacto.
    }
    /**
     * Método (Procedimento) para ENCAPSULAR o número de telemóvel (SETTER).
     * @param  telemovel   Parâmetro telemóvel a ser "encapsulado".
     * @return             não tem retorno. (Void - Vazio).
     */
    public void setTelemovel(Double telemovel){
        // encapsulamento propriamente dito.
        this.Tlm = telemovel;
    }
    /**
     * Método (Procedimento) para ENCAPSULAR o Email do Contacto (SETTER).
     * @param   email       Parâmetro email a ser "encapsulado".
     * @return              não tem retorno. (Void - Vazio).
     */
    public void setEmail(String email){
        // encapsulamento propriamente dito.
        this.Email = email;
    }
    /**
     * Método (Função) para extrair o Telemóvel do ENCAPSULAMENTO da Classe Contacto (GETTER)
     * @param               Método sem parâmetros.
     * @return  Tlm         Retorna o Atributo Tlm da Classe.
     */
    public double getTlm(){
        // retorno da propriedade "encapsulada".
        return this.Tlm;
    }
    /**
     * Método (Função) para extrair o Email do ENCAPSULAMENTO da Classe Contacto (GETTER)
     * @param               Método sem parâmetros.
     * @return  Email       Retorna o Atributo Email da Classe.
     */
    public String getEmail(){
        // retorno da propriedade "encapsulada".
        return this.Email;
    }
}