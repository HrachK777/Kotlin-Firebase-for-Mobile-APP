# Roastly 🔥

Uma plataforma de feedback profissional onde os utilizadores podem dar e receber "roasts" (avaliações) sobre competências profissionais.

## 📱 Sobre a Aplicação

O Roastly é uma aplicação Android que permite aos utilizadores:

- **Dar Feedback**: Avaliar colegas em competências como colaboração, iniciativa, responsabilidade e conhecimento
- **Receber Feedback**: Receber avaliações construtivas de outros utilizadores
- **Visualizar Estatísticas**: Acompanhar médias pessoais e evolução ao longo do tempo
- **Rankings**: Ver classificações e destaques mensais
- **Gestão de Perfil**: Personalizar perfil com foto e informações profissionais

## 🛠️ Tecnologias

- **Android**: Kotlin, ViewBinding, Navigation Component
- **Backend**: Firebase, Firestore
- **Autenticação**: Firebase Auth
- **Storage**: Firebase Storage para imagens de perfil
- **Arquitetura**: MVVM com Repository Pattern

## 📚 Documentação

Para informações detalhadas sobre conceitos avançados e funcionalidades:

### [📖 Documentação Técnica](docs/)

- **[O que são Agentes?](docs/agentes.md)**: Explicação completa sobre agentes de software e suas aplicações
- **[Agentes no Roastly](docs/agentes-roastly.md)**: Como implementar agentes inteligentes na plataforma

## 🚀 Funcionalidades

### ✅ Implementadas
- Sistema de autenticação (login/registo)
- Perfis de utilizador com fotos
- Sistema de avaliações com 4 competências
- Cálculo automático de médias
- Histórico de feedback
- Rankings e estatísticas
- Edição de perfil
- Eliminação de conta

### 🔄 Em Desenvolvimento
- Agentes de análise de feedback
- Recomendações inteligentes
- Moderação automática
- Notificações personalizadas

## 📁 Estrutura do Projeto

```
app/src/main/java/ly/roast/roastly/
├── ui/                     # Interface do utilizador
│   ├── login/             # Atividades de login/registo
│   ├── profile/           # Fragmento de perfil
│   └── common/            # Atividades comuns (Home, Splash)
├── data/                  # Camada de dados
│   └── repository/        # Repositórios para acesso a dados
├── viewmodel/             # ViewModels (MVVM)
├── viewmodelFactories/    # Factories para ViewModels
└── utils/                 # Utilitários gerais
```

## 🔧 Configuração

1. **Clone o repositório**
   ```bash
   git clone https://github.com/ruigpribeiro/Roastly.git
   ```

2. **Configure o Firebase**
   - Adicione o ficheiro `google-services.json` na pasta `app/`
   - Configure Firestore, Authentication e Storage

3. **Build e Execute**
   ```bash
   ./gradlew build
   ```

## 👥 Contribuições

Contribuições são bem-vindas! Por favor:

1. Faça fork do projeto
2. Crie um branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para o branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📄 Licença

Este projeto é desenvolvido para fins educacionais e profissionais.

---

*Desenvolvido com ❤️ pela equipa Roastly*