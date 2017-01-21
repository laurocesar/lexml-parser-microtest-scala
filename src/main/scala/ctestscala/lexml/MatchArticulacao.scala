package ctestscala.lexml

import br.gov.lexml.parser.pl.block.Alteracao
import br.gov.lexml.parser.pl.block.Block
import br.gov.lexml.parser.pl.block.Dispositivo
import br.gov.lexml.parser.pl.block.OL
import br.gov.lexml.parser.pl.block.Omissis
import br.gov.lexml.parser.pl.block.Paragraph
import br.gov.lexml.parser.pl.block.Table
import br.gov.lexml.parser.pl.block.Unrecognized

object MatchArticulacao {

  def blocksString(b: List[Block]): List[String] = 
    b.map { blockString } 
    
  private def blockString(b : Block): String = b match {
      case e: Unrecognized =>      "Unrecognized: " + e + printChildren(e) 
      case e: Alteracao =>         "Alteracao: "    + e + printChildren(e)
      case e: Table =>             "Table: "        + e + printChildren(e)
      case e: OL =>                "OrderedList: "  + e + printChildren(e)
      case e: Omissis =>           "Omissis: "      + e + printChildren(e)
      case e: Dispositivo =>       "Dispositivo: "  + e + printChildren(e) + "          \n" + e.subDispositivos.map(blockString).mkString("          \n")
      case e: Paragraph =>         "Paragraph: "    + e + printChildren(e)
      case _ => "Possible match for Block, Image..."
  }
  
  private def printChildren(e: Block) = e.children.map(blockString).mkString("\n   ")

}