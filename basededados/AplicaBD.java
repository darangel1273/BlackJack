package basededados;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Exemplo de Aplicacao em Java, para criar uma #moldura (JFrame)
 * e testar o acesso à base de dados mysql @see #db #DBHelper
 * se a ligacao for bem-sucedida carrega os registos da tabela pessoa para o ArrayList<basededados.Pessoa> com o nome: #PessoaS
 *
 * @author Ruin Prier
 * @version 4.0
 */
public class AplicaBD {
	JFrame moldura;
	JPanel painel;
	JTextField nome, apelido, morada, dataNasc, anos;
	JButton guardar, enviar;
	JMenuBar barra;
	JLabel img;
	DBHelper db;
	int posicao;
	ImageIcon ico;
	JTextArea area;
	JMenu fich, registos, ajuda;
	JMenuItem novo, apagar, reload, sobre, anterior, seguinte, primeiro, ultimo;
	ArrayList<Pessoa> PessoaS;
	Boolean vaiInserir;

	/**
	 * Metodo construtor para instanciar e construir em memória, as propriedades desta classe.
	 */
	public AplicaBD() {
		vaiInserir = false;                                 // flag para controlar se o botao "guardar vai atualizar ou inserir um registo na BD
		posicao = 0;                                       //  Indice para controlo de navegacao nos registos, dentro do array dinamico #PessoaS
		refresh();                                          //  Preenche o array com os registos da tabela de Pessoas.
		construirJanela();
		//  Implementa a interface "ActionListener" de forma indirecta
		//  que obriga a reescrever ( a criar ) o corpo do método
		primeiro.addActionListener(e -> {               //  declarado na interface: "#actionPerformed( ActionEvent e)"
			posicao = 0;                                //  posicionar o ArrayList<Pessoa> na 1.ª "Pessoa" do Array.
			carregaForm();                              // vai copiar para as caixas de texto do Form, a pessoa do array na posicao 0
		});
		ultimo.addActionListener(e -> {
			posicao = PessoaS.size() - 1;
			carregaForm();                              // vai copiar para as caixas de texto do Form, a pessoa do array na ultima posicao
		});
		seguinte.addActionListener(e -> {
			if ((PessoaS.size()) != posicao) {//Valida que nao e a ultima posicao, senao transborda #ArrayIndexOutOfBoundsException
				posicao++;
				carregaForm();                          // vai copiar para as caixas de texto do Form, a pessoa do array na posicao seguinte
			}
		});
		anterior.addActionListener(e -> {
			if ( PessoaS.size() != 0 ) {                  //Valida que nao e a primeira posicao do ArrayList<Pessoa>, senao o indice fica -1
				posicao--;                              // e se assim for, da erro. #ArrayIndexOutOfBoundsException
				carregaForm();                          // vai copiar para as caixas de texto do Form, a pessoa do array na posicao anterior
			}
		});
		novo.addActionListener(e -> {
			vaiInserir = true;                          // Vai fazer INSERT  e não um UPDATE (no SQL do DBHelper)
			apelido.setText("");
			dataNasc.setText("yyyy-MM-dd");
			morada.setText("");
			nome.setText(""); //limpa as caixas
			guardar.setText("Inserir");
		});
		//Vai buscar as variáveis de Ambiente
		sobre.addActionListener( e -> {
			area = new JTextArea();                     // Cria uma area de texto.
			area.setBounds( new Rectangle( 485, 1, 1900, 1100 ) ); //define os limites da area de texto.
			painel.add(area);                           //Adiciona uma area de texto ao painel
			area.setVisible( true );    // Mostra a area de texto
			Map<String,String> mapS = System.getenv() ;
			Set<String> chaveS = mapS.keySet();
			for ( String k : chaveS )
				area.append("\n " + k + ":\t\t\t" + mapS.get(k) );
		});
		guardar.addActionListener(e -> {
			Pessoa p = new Pessoa(apelido.getText(), Date.valueOf(dataNasc.getText()), morada.getText(), nome.getText(), PessoaS.get(posicao).getNumero());
			p.setPath(PessoaS.get(posicao).getPath());    //Guardar no objeto p: o caminho até à imagem
			PessoaS.set( posicao, p );                      //Atualiza o array com o objeto p, na posição.
			if ( vaiInserir ) {
				db.inserePessoa(p);
			} else {
				db.updPessoa(p);
			}   //Se clicou antes no menu "novo" vai inserir o registo senão so atualiza o registo atual
			vaiInserir = false;
			refresh();
			guardar.setText("Actualizar");
		});
		apagar.addActionListener(e -> {
			db.delPessoa(PessoaS.get(posicao));
			JOptionPane.showMessageDialog(moldura, "Registo:" + PessoaS.get(posicao).getNumero() + " Apagado");
			refresh();
		});
		reload.addActionListener(e -> refresh());

		enviar.addActionListener(e -> {
			JFileChooser abre = new JFileChooser();
			abre.setFileFilter(new FileNameExtensionFilter("Imagens JPEG", "jpg"));
			try {
				if (abre.showOpenDialog(moldura) == JFileChooser.APPROVE_OPTION) {  // igual a zero
					File fich = abre.getSelectedFile();
					InputStream is = new FileInputStream(fich);
					PessoaS.get(posicao).setBImage(ImageIO.read(is));
					ico.setImage(PessoaS.get(posicao).getFoto());
					PessoaS.get(posicao).setPath(fich.getAbsolutePath());
					painel.repaint();
				}
			} catch (IOException ioe) {
				System.err.printf("\n %s \t %s ", ioe.getLocalizedMessage() , ioe.getMessage() ) ; //impressão dos erros num "LOG"
				throw new RuntimeException(ioe);
			}
		});
	}

	void construirJanela() { //procedimento para construir os elementos gráfcos da Aplicação
		moldura = new JFrame("Mysql: Pessoa");         //  Cria uma janela de Windows Moldura @see #moldura sera o contentor do #painel
		moldura.setBounds(1, 1, 1900, 1100); //  Define o tamanho da Moldura, que ira conter o #painel.
		painel = new JPanel();                              //  Cria um novo painel @see #painel JPanel para levar o rotulo da imagem).
		painel.setLayout(null);                           //  Nao define Layout para o #painel (JPanel).
		painel.setBounds(1, 1, 1850, 1050);//  Define o tamanho da Moldura.
		nome = new JTextField("Nome");                      //  Cria uma caixa de texto JTextField #nome que tera o nome a extrair do campo da base de dados.
		nome.setBounds(10, 5, 150, 25);    //  Define o tamanho (e os limites) da caixa de texto: (x,y, largura , altura )
		apelido = new JTextField("Apelido");                //  Caixa que vai conter o campo "apelido" da base de dados ;
		apelido.setBounds(10, 35, 150, 25);//  Tamanho Igual ao nome (mas a coordenada "Y" fica mais abaixo).
		morada = new JTextField(" Morada ");               // Cria uma caixa de texto JTextField #morada que tera o nome a extrair do campo da base de dados.
		morada.setBounds(10, 65, 150, 25);// Define o tamanho do JTextField #morada (igual aos anteriores)
		dataNasc = new JTextField("Data de Nascimento ");  // Cria uma caixa de texto JTextField #dataNasc que tera o nome a extrair do campo da base de dados.
		dataNasc.setBounds(10, 100, 150, 25);//  Define o tamanho do JTextField #dataNasc (igual aos anteriores)
		anos = new JTextField("Anos") ;                     // Cria uma caixa de texto JTextField #anos que sera calculado automaticamente.
		anos.setBounds(160,100,30,25 );     // Define o limite da caixa dos anos.
		guardar = new JButton("Actualizar");            // Cria um botao (nao faz nada) apenas serve para exemplificar um dos "escutadores" @see #ActionListener de accao.
		guardar.setBounds(10, 130, 150, 25);//  Define o tamanho do JButton (igual aos anteriores)
		ico = new ImageIcon("./NotFound.png");//  Cria um icone que e inicializado com uma imagem qualquer, posteriormente contera o campo "foto" da tabela de Pessoa.
		img = new JLabel(ico );                              //  Cria um rotulo (JLabel) @see #img que vai levar depois a imagem carregada da base de dados.
		img.setBounds(175, 1, 300, 250);  //  Define o tamanho da imagem no rótulo.
		enviar = new JButton("Imagem...");          //  Cria um botão que peda uma imagem.
		enviar.setBounds(175, 200, 150, 25); // define os limites do botao de caregar imagem.
		barra = new JMenuBar();                         //  Cria uma barra de menus vazia (por enquanto) ;
		fich = new JMenu("Ficheiro");                //  Cria o Menu "Ficheiro". @see #fich
		registos = new JMenu("Registos");            //  Cria o Menu "Registos". @see #registos
		ajuda = new JMenu("Ajuda");                  //  Cria o Menu "Ajuda".    @see #ajuda
		novo = new JMenuItem("Novo");            //  Cria uma opcao para por dentro de um menu, neste caso: Novo @see #novo
		apagar = new JMenuItem("Apagar");          //  Cria uma opcao para por dentro de um menu, neste caso: Novo @see #apagar
		reload = new JMenuItem("Refrescar");        //refrescar o array com a tabela da BD
		sobre = new JMenuItem("Sobre");          //  Cria uma opcao para por dentro de um menu, neste caso: Ajuda. @see #sobre
		primeiro = new JMenuItem("Primeiro");    //  Cria uma opcao para por dentro de um menu, neste caso: Registos @see #primeiro
		anterior = new JMenuItem("Anterior");    //  Cria uma opcao para por dentro de um menu, neste caso: Registos @see #anterior
		seguinte = new JMenuItem("Seguinte");    //  Cria uma opcao para por dentro de um menu, neste caso: Registos @see #seguinte
		ultimo = new JMenuItem("Ultimo");         //  Cria uma opcao para por dentro de um menu, neste caso: Registos @see #ultimo
		painel.add(nome);                             //  Adiciona a caixa de texto JTextField #nome      ao #painel
		painel.add(apelido);                          //  Adiciona a caixa de texto JTextField #apelido   ao #painel
		painel.add(morada);                            //  Adiciona a caixa de texto JTextField #morada   ao #painel
		painel.add(dataNasc);                          //  Adiciona a caixa de texto JTextField #dataNasc   ao #painel
		painel.add(guardar);                          //  Adiciona o botao JButton #guardar               ao #painel
		painel.add(enviar);                             // Adiciona o botão JButton #enviar                 ao painel
		painel.add(anos) ;                              // Adiciona a cx de texto dos anos              ao painel
		painel.add(img);                             //  Adiciona o botao JLabel  #img                   ao #painel
		fich.add(novo);                               //  Adiciona a opcao #novo (JMenuItem)              ao menu #fich
		fich.add(apagar);                             //  Adiciona a opcao #apagar (JMenuItem)              ao menu #fich
		fich.add(reload);                               //  Adiciona a opcao #refresh (JMenuItem)              ao menu #fich
		ajuda.add(sobre);                             //  Adiciona a opcao #sobre(JMenuItem)              ao menu #ajuda
		registos.add(primeiro);                      //  Adiciona a opcao #primeiro(JMenuItem)           ao menu #registos
		registos.add(anterior);                      //  Adiciona a opcao #anterior(JMenuItem)           ao menu #registos
		registos.add(seguinte);                      //  Adiciona a opcao #seguinte(JMenuItem)           ao menu #registos
		registos.add(ultimo);                        //  Adiciona a opcao #ultimo (JMenuItem)            ao menu #registos
		barra.add(fich);                              //  Adiciona o menu  #fich (JMenu) a barra de menus (JMenuBar) #barra
		barra.add(registos);                          //  Adiciona o menu  #registos (JMenu) a barra de menus (JMenuBar) #barra
		barra.add(ajuda);                             //  Adiciona o menu  #ajuda (JMenu) a barra de menus (JMenuBar) #barra
		painel.add(barra);                            //  Adiciona a Barra de Menus (JMenuBar) #barra ao #painel (JPanel).
		moldura.add(painel);                        //  Adiciona o JPanel #painel a JFrame
		moldura.setJMenuBar(barra);                   //  So a frame e pode levar a JMenuBar, o JPanel nao pode.
		//moldura.setLayout( null );
		moldura.setVisible(true);                     // Torna a moldura visivel.
	}

	public void carregaForm() {                             //cópia para o form., o registo no indice atual do Array: #PessoaS
		nome.setText(PessoaS.get(posicao).getNome());       // Poe na cx. De texto #nome, o retorno do metodo #getNome
		apelido.setText(PessoaS.get(posicao).getApelido()); //Faz a mesma coisa para o #apelido
		morada.setText(PessoaS.get(posicao).getMorada());   //Faz a mesma coisa para a #morada
		dataNasc.setText(PessoaS.get(posicao).getDataNasc().toString());//Faz a mesma coisa para a #DataNasc
		anos.setText( PessoaS.get(posicao).getAnos() + " A" );  // Buscar a idade à classe de Pessoa
		ico.setImage(PessoaS.get(posicao).getFoto()); // Altera o icone do rotulo para levar o retorno do metodo #getFoto
		// img.setText("Sem imagem"); ico =null;
		painel.repaint();                               //"refresh" do painel para actualizar a imagem.
	}

	/**
	 * Recarrega o array PessoaS
	 */
	public void refresh() {
		if (db != null) db.fechar();// Fecha ligações anteriores, se existirem.
		db = new DBHelper();        // Abre a ligacao a base de dados do mysql.
		PessoaS = new ArrayList<>();// Cria um Array Dinamico de Pessoas @see Pessoa.java.
		PessoaS = db.queryPessoa(); // Preenche o array com os dados da Tabela pessoa @see #DBHelper;
	}

	/**
	 * @see #AplicaBD
	 * @deprecated Metodo para testar o acesso à base de dados do mysql e criar a JFrame #moldura
	 */
	@Deprecated
	public static void main(String[] args) {
		new AplicaBD();
	}
}