package ucanacess;
import basededados.*; //Usa o pacote anterior criado por nos, por causa da classe Pessoa.java que e implementada aqui
import java.util.Collection;
import java.sql.*;
import java.util.logging.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.awt.event.*;
import javafx.application.*;
import javafx.stage.*;
import javax.swing.*;
import java.io.*;
import java.util.*;

/**
 * Esta classe MicrosoftAcess está num pacote separado e é uma alternativa à Classe DBHelper do pacote "basededados" 
 * serve para aceder a uma base de dados em Microsoft Acess do Microsoft Office. 
 * Não precisa de ter referência no CLASSPATH, o "Jconnector-mysql.jar" ou de ter esta dependencia carregada.
 * <p>
 * ESTE PACOTE DEPENDE DA CLASSE PESSOA DO PACOTE "basededados" @see #basededados
 * Esta classe IMPORTA o pacote o pacote basededados pois precisa da classe Pessoa, para criar o mesmo ArrayList<Pessoa> PessoaS,
 * no entanto o controlador que vem substituir o "odbc data source" do Windows, precisa de outras dependencias 
 * que necessitam de ser carregadas como blibliotecas ou referênciadas no ( CLASSPATH ):
 * 1) ./ucanaccess-5.0.1.jar
 * 2) ./lib/commons-lang3-3.8.1.jar
 * 3) ./lib/commons-logging-1.2.jar
 * 4) ./lib/hsqldb-2.5.0.jar
 * 5) ./lib/jackcess-3.0.1.jar
 * 
 * A base de dados É LOCAL : Ex: "c:/pis20.accdb" ( Logo evita Excepções de Rede ) e contém uma tabela "pessoa"
 * 
 * @author ( Ruin Mantel Die Oliver Prier ) 
 * @version (2.0)
 */
public class MicrosoftAcess{
    private static final String controlador = "net.ucanaccess.jdbc.UcanaccessDriver" ;
    private static final String bd="jdbc:ucanaccess://C:/Users/Utilizador/DB/pis20.accdb;memory=true";
    private static final String qry_pessoa = "select apelido,datanasc,foto,morada,nome,numero from pessoa;"; 
    private static final String user = "admin";
    private static final String pass = "admin"; 
    private Connection liga = null; 
    private Statement sql = null;
    private ResultSet res = null ; 
    private ArrayList<Pessoa> PessoaS;
    private int posicao;
    /**
     * Construtor que carrega os controladores JDBC para o Microsoft Acess (contidos nos 5 ficheiros jar mencionados acima)
     * 
     */
    public MicrosoftAcess(){
        PessoaS = new ArrayList<Pessoa>();
        try { 
            Class.forName( controlador );//controlador que faz a ponte "jdbc" entre o java e o microsft acess
            liga = DriverManager.getConnection(bd,user,pass);//Faz a ligação e autentica com o user:"admin" e pass"admin" geral do acess
            sql = liga.createStatement(); //Prepara a execução de um comando SQL
        }
        catch (ClassNotFoundException cnfe){ 
            cnfe.printStackTrace();// #Exception se os controladores não foram carregados
        }
        catch (SQLException sqle){ 
            sqle.printStackTrace();// #Exception se: 1) Base de dados não encontrada;2)autenticação errada
        }
    }
    /**
     * Método que executa uma consulta SQL à tabela de "pessoa" e retorna uma grelha de resultados -> CURSOR ou ResultSet
     * contém um ciclo que percorre cada um dos registos de #Pessoa carregados da tabela pessoa na BD e "enche" um Array Dinâmico
     * (ArrayList<Pessoa>) com o nome "#PessoaS" Para testar o carregamento de imagens, foi criado um painel para cada indice 
     * do Array de Pessoas
     */
    public ArrayList<Pessoa> queryPessoa(){
        try {
            res = sql.executeQuery(qry_pessoa);
            posicao = 0;
            while( res.next() ){
                PessoaS.add( new Pessoa( res.getString(1), res.getDate(2), res.getBlob(3) ,res.getString(4), 
                                                                                        res.getString(5), res.getDouble(6) ) );
                System.out.printf("\n %s \t %s \t %f \t %s\t\t%s:", PessoaS.get(posicao).getNome() , PessoaS.get(posicao).getApelido(), 
                  PessoaS.get(posicao).getNumero() , PessoaS.get(posicao).getDataNasc().toString(), PessoaS.get(posicao).getFoto().toString() );
                JFrame m = new JFrame("Carregar do Microsoft Acess");//Cria uma moldura #m (JFrame)               
                JPanel p = new JPanel(); //Cria um Painel #p (JPanel)
                p.setBounds( 1, 1, 1920, 1200 ); //define tamanho e limites para o painel p
                p.setLayout(null);//nao define layout para este painel    
                JLabel r = new JLabel(); //cria um rótulo #r (JLabel)
                ImageIcon ico = new ImageIcon( PessoaS.get(posicao).getFoto() ) ;//Carrega a foto encapsulada no Icone
                r.setIcon(ico); //define a imagem no rótulo 
                r.setBounds( 10, 10, 1900, 1200 ); //define um tamanho grande para o rotulo
                p.add( r );     //adiciona o rotulo ao painel
                m.add(p);   //Adiciona o painel p a moldura m
                m.repaint();//"refresh" da moldura (facultativo??)
                m.setSize( 1920, 1200 ); //define tamanho da moldura
                m.setVisible(true); //torna a moldura visivel
                posicao++; //incrementa a posicao do indice no array para carregar o próximo registo com a proxima pessoa no ArrayList
            }
        }
        catch ( SQLException sqle){
            sqle.printStackTrace(); //Apanha erros ( #SQLException ) de sql que tenham ocorrido
        }
        catch ( Exception e){
            e.printStackTrace(); //Apanha restantes erros ( #Exception ) tal como erro na conversão de imagem
        }
        finally{ return PessoaS ;} //retorna o que tiver no arraylist (com erros ou nao) nem que seja null
    }
    public void fechar(){
        try{
            sql.close();// fecha o envio de comandos SQL a base de dados (previne parte de SQL INJECTION)
            liga.close(); //Fecha a ligacao a Base de dados
        }
        catch (SQLException sqle){
            sqle.printStackTrace();//Apanha erros a fechar a ligacao
        }
    }
    /**
     * Metodo para executar a consulta SQL a tabela de pessoa na base de dados pis20.accdb
     */
    public static void main(String[] args){ MicrosoftAcess a= new MicrosoftAcess(); a.queryPessoa(); a.fechar(); }
}