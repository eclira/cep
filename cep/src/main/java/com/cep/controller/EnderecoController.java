package com.cep.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;

import com.cep.model.Endereco;
import com.cep.repository.EnderecoRepository;

@Controller
public class EnderecoController {
	
	@Autowired
	EnderecoRepository enderecoRepository;
		
	@GetMapping({"/",""})
	public String paginaInicial(@Param("pesquisa") String pesquisa, Endereco enderecos, RedirectAttributes redirect) {		
		String valida = "";
		enderecos.setCep(pesquisa);		
		
		if(enderecos.getCep() != null) {
			
			pesquisaCep(enderecos, valida);
			if(valida == "") {
				redirect.addFlashAttribute("msgExito", "CEP ou Endereco naõ localizado!");
			}			
		}		
		return "cadastroEndereco";
	}
	
	@GetMapping("/novo")
	public String novo(Model model) {
		model.addAttribute("enderecos", new Endereco());
		return "novo";
	}
	
	@PostMapping("/")
	public String salvar(@Validated Endereco endereco, BindingResult bindingResult, RedirectAttributes redirect, Model model) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("endereco", endereco);
			return "cadastroEndereco";
		}
		
		enderecoRepository.save(endereco);
		redirect.addFlashAttribute("msgExito", "Endereco adicionado com sucesso!");
		return "redirect:/";
		
	}	
	
	
	public String pesquisaCep(Endereco endereco, String valida) {		
		try {			
			
			URL url = new URL("https://viacep.com.br/ws/"+endereco.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}			
			Endereco enderecoCep = new Gson().fromJson(jsonCep.toString(), Endereco.class);			
			
			endereco.setCep(enderecoCep.getCep());
			endereco.setLogradouro(enderecoCep.getLogradouro());
			endereco.setComplemento(enderecoCep.getComplemento());
			endereco.setBairro(enderecoCep.getBairro());
			endereco.setLocalidade(enderecoCep.getLocalidade());
			endereco.setUf(enderecoCep.getUf());
			endereco.setIbge(enderecoCep.getIbge());
			endereco.setGia(enderecoCep.getGia());
			endereco.setDdd(enderecoCep.getDdd());
			endereco.setSiafi(enderecoCep.getSiafi());		
			
			valida = enderecoCep.getLogradouro();	
			
		
			
		}catch (Exception e) {
			e.printStackTrace();
		}		
		return "cep";
	}
	

}


