package ctestscala.lexml

import java.io.InputStream

import scala.xml.Text

import br.gov.lexml.parser.pl.ProjetoLeiParser
import br.gov.lexml.parser.pl.block.Block
import br.gov.lexml.parser.pl.block.Paragraph
import br.gov.lexml.parser.pl.errors.ParseProblem
import br.gov.lexml.parser.pl.metadado.Metadado
import br.gov.lexml.parser.pl.output.LexmlRenderer
import br.gov.lexml.parser.pl.validation.Validation
import br.gov.lexml.parser.pl.block.Unrecognized
import br.gov.lexml.parser.pl.block.Image
import br.gov.lexml.parser.pl.block.OL
import br.gov.lexml.parser.pl.block.Dispositivo
import br.gov.lexml.parser.pl.block.Alteracao
import br.gov.lexml.parser.pl.block.Omissis
import br.gov.lexml.parser.pl.block.Table

object App extends App {

  val inTXT = HelperIO.conteudoLei("PRT834-2016-Validation.txt")
  val txt = scala.io.Source.fromInputStream(inTXT).getLines().toList
  val txtParagraphList = txt.map(x=> Paragraph(Seq(Text(x))))

  val profile = HelperProfile.profileBySigla("PRT")
  val metadado = Metadado(profile, hashFonte = None)
  
  val parser = new ProjetoLeiParser(profile)
  
  
  //executa o parser e obtem a articulação
  val articulacao : List[Block] = parser.parseArticulacao(txtParagraphList, false)
  
  //imprime xml da articulação
  println ( LexmlRenderer.renderArticulacao(articulacao).toString )
  
  println("Articulacao Validation:")
  Validation.validaEstrutura(articulacao).foreach { printParseProblem }
  
  println
  println("Match em articulacao:")
  MatchArticulacao.blocksString(articulacao).foreach(println)  

  def printParseProblem(x: ParseProblem) = {
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
  import br.gov.lexml.parser.pl.profile.ProjetoDeLeiDoSenadoNoSenado


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
    case "PLS" => ProjetoDeLeiDoSenadoNoSenado
    case _ => ProjetoDeLeiDoSenadoNoSenado
  }

}


object HelperIO {
  
  def conteudoLei(fileName: String): InputStream = {
    Thread.currentThread().getContextClassLoader().getResourceAsStream("leis/" + fileName);
  }
  
}