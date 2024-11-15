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
     * M�todo (Procedimento) para ENCAPSULAR o n�mero de telem�vel (SETTER).
     * @param  telemovel   Par�metro telem�vel a ser "encapsulado".
     * @return             n�o tem retorno. (Void - Vazio).
     */
    public void setTelemovel(Double telemovel){
        // encapsulamento propriamente dito.
        this.Tlm = telemovel;
    }
    /**
     * M�todo (Procedimento) para ENCAPSULAR o Email do Contacto (SETTER).
     * @param   email       Par�metro email a ser "encapsulado".
     * @return              n�o tem retorno. (Void - Vazio).
     */
    public void setEmail(String email){
        // encapsulamento propriamente dito.
        this.Email = email;
    }
    /**
     * M�todo (Fun��o) para extrair o Telem�vel do ENCAPSULAMENTO da Classe Contacto (GETTER)
     * @param               M�todo sem par�metros.
     * @return  Tlm         Retorna o Atributo Tlm da Classe.
     */
    public double getTlm(){
        // retorno da propriedade "encapsulada".
        return this.Tlm;
    }
    /**
     * M�todo (Fun��o) para extrair o Email do ENCAPSULAMENTO da Classe Contacto (GETTER)
     * @param               M�todo sem par�metros.
     * @return  Email       Retorna o Atributo Email da Classe.
     */
    public String getEmail(){
        // retorno da propriedade "encapsulada".
        return this.Email;
    }
}