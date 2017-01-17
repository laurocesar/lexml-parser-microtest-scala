package ctestscala.lexml

import java.io.InputStream

import scala.xml.Text

import br.gov.lexml.parser.pl.ProjetoLeiParser
import br.gov.lexml.parser.pl.block.Paragraph
import br.gov.lexml.parser.pl.output.LexmlRenderer
import br.gov.lexml.parser.pl.metadado.Metadado
import br.gov.lexml.parser.pl.ProjetoLei
import br.gov.lexml.parser.pl.errors.ParseProblem

object App extends App {

  val profile = HelperProfile.profileBySigla("PRT")
  val inTXT = HelperIO.conteudoLei("LeiNumeroLetra.txt")
  val txt = scala.io.Source.fromInputStream(inTXT).getLines().toList
  val metadado = Metadado(profile, hashFonte = None)
  
  val parser = new ProjetoLeiParser(profile)
  
  
  
  //executa o parser e obtem a articulação
  val articulacao = parser.parseArticulacao(txt.map(
      x=> Paragraph(Seq(Text(x)))), false)
  // imprime xml da articulação
  println ( LexmlRenderer.renderArticulacao(articulacao).toString )    
  
  
  
  //obtem a estrutura completa, inclusive a lista de problemas do validation
  val parserRet : (Option[ProjetoLei], List[ParseProblem]) = parser.fromBlocks(metadado, articulacao)
  
  println
  println("Problemas encontrados:")
  parserRet._2.foreach { x => 
    println ("msg: " + x.msg
        + "; posição: " + x.pos.mkString(" ")
        + "; Categoria: " + x.problemType.category.code + " - "+ x.problemType.category.description
        + "; Tipo : " + x.problemType.code + " - "+ x.problemType.description)
  }
  
}

/**
 * Estende os profiles em DocumentProfile.scala e
 * permite obter os profiles adequados conforme os tipos de norma do connexus 
 */
object HelperProfile {

  import br.gov.lexml.parser.pl.profile.Decreto
  import br.gov.lexml.parser.pl.profile.DefaultRegexProfile
  import br.gov.lexml.parser.pl.profile.DocumentProfile
  import br.gov.lexml.parser.pl.profile.EpigrafeOpcional
  import br.gov.lexml.parser.pl.profile.FederalProfile
  import br.gov.lexml.parser.pl.profile.Lei
  import br.gov.lexml.parser.pl.profile.LeiComplementar

  // 
  object Portaria extends DocumentProfile with DefaultRegexProfile with FederalProfile with EpigrafeOpcional {
    override def urnFragTipoNorma = "portaria"
    override def epigrafeHead = "PORTARIA"
  }

  /**
   * Retorna o tipo de profile correto de acordo com o tipo da norma
   * (http://localhost/#/tipo-norma)
   */
  def profileBySigla(siglaProfile: String): DocumentProfile = siglaProfile match {
    case "PRT" => Portaria
    case "LEI" => Lei
    case "DEC" => Decreto
    case "LC" => LeiComplementar
  }

}


object HelperIO {
  
  def conteudoLei(fileName: String): InputStream = {
    Thread.currentThread().getContextClassLoader().getResourceAsStream("leis/" + fileName);
  }
  
}